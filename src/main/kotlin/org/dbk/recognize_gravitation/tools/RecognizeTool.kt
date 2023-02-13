package org.dbk.recognize_gravitation.tools

import kotlin.math.cos
import kotlin.math.sin


class RecognizeTool<P : IPoint<P>, A : AttractionValue<A>>(
    private val catalog: List<Block<P, A>>,
    private val forceK: Double = 1.0,// коэффициент усиления
    val distanceDegree: Double = 2.0,//степень расстояния
    private val angleK : Double = 1.0,
    private val shiftK : Double = 1.0,
    private val weightFunction: WeightFunction = WeightFunction.OpenContour,//функция веса может быть скаляром, может быть вектором
    private val proximityLevel: Double = 1.0,
    private val fragmentation: Int = 1// from 1 to 2,3,4
) {

    private val masses: List<MassObject<P, A>>;

    init {
        masses = mapToMasses(catalog)
    }


    /**
     * Для примитивов
     * 1. Найти среднею точку каждого элемента.
     * 2. Разбить на секции каждый элемента(n - секций)
     *
     *
     * Для сложных объектов
     * (0. Нормировать. Перебрать под разными углами)
     * 1. Посчитать силу притяжения каждого объекта к каждому объекту эталона.
     * F= k* L1*L2 / r^t. (t = 2, в первом приближении)
     * 2. Найти среднею точку и смещения цента объекта относительно центра эталона
     * 3. Найти силы от каждого к каждому элементу, найти среднею силу для каждого элемента
     * 4. Посчитать суммарный момент вокруг центра фигуры.
     * 5. Рассчитать скорость и угол поворота, с демпфированием. До тех пор, пока расстояние больше порога.
     * 6. Найти расположения объект, максимизирующий момент, в диапазоне от 0 до 360
     * 7. softmax для определения вероятности корректного распознавания
     *
     *
     * Нужен параметрический поиск для оптимизации
     *  - k коэффициент усиления,
     *  - t степень расстояния,
     *  - f(graph) - функции массы, длина(площадь для 3д) каждого элемента или описывающая площадь(объем для 3д). (вопрос могут ли попасться тонкостенные элементы без указания толщины?)
     *  - n - фрагментация элементов для лучшего совпадения.
     *
     *
     *  Нужна матричная оптимизация. Список масс, их значений и координат представить в виде матрицы для эталона и для объекта для распознавания.
     *  Далее все действия делать над матрицами, дабы избежать выделение памяти при операции над векторами.
     *
     */
    fun recognize(graphs: List<Graph<P, A>>): Pair<Double, String>? {
        val mass = mapToMass(graphs)

        val results = masses.map { Pair(findNearestPosition(it, mass), it.name) }
        return results.maxByOrNull { it.first }
    }



    fun findNearestPosition(mass: MassObject<P, A>, baseMass: MassObject<P, A>): Double {
        var angle = 0.0
        val angleVelocity = 0.0
        var shiftPosition =  mass.point.zero()
        var velocity = mass.point.zero()

        var firstMass = mass
        val dir = baseMass.point.subtract(firstMass.point)
        var resultantForce: P
        do {

            val resultantForcesByItems = firstMass.masses
                .map { firstItem ->
                    val resultantForceByItem = baseMass.masses.asSequence()
                        .map { anotherItem -> force(firstItem, anotherItem) }
                        .reduce { acc, p -> acc + p }
                    Pair(firstItem, resultantForceByItem)
                }
            resultantForce = resultantForcesByItems.asSequence()
                .map { it.second }
                .reduce { acc, p -> acc + p }
            val center = firstMass.point

            resultantForce += (velocity * (-1.0) * 10.0)
            val resultantMoment = resultantForcesByItems.asSequence()
                .map { pair ->
                    val position = pair.first.point
                    val force = pair.second
                    val forceVector = Vector(position, position.add(force))
                    val perpendicularToLine = center.perpendicularToLine(forceVector)
                    val moment = perpendicularToLine.crossProduct(force)
                    moment
                }.reduce { acc, p3 -> acc + p3 }

            val inertia = firstMass.value
            val momentOfInertia = firstMass.momentOfInertiaAroundOfAxis(resultantMoment)
            //delta = acceleration * deltaTime
            var deltaPosition: P = mass.point.zero()
            if (center is Point) {
                val deltaAngular = resultantMoment.z / momentOfInertia
                deltaPosition = (resultantForce / inertia.absoluteValue()) * shiftK
                angle += angleK * deltaAngular // todo 3 коэффициента по силе, по смещению и по углу поворота
                velocity += deltaPosition
                shiftPosition += velocity * shiftK
            } else {
                TODO("Not implement for P3")
            }

            firstMass =  firstMass.rotateAndMove(angle, center, shiftPosition)
        } while (dir.length() > proximityLevel)



        return resultantForce.length()

    }

    private fun rotateAndMove(
        angle: Double,
        firstMass: MassObject<P, A>,
        center: P,
        shiftPosition: P
    ): MassObject<P, A> {
        val sin = sin(Math.PI / 180 * angle)
        val cos = cos(Math.PI / 180 * angle)
        return MassObject(firstMass.name, firstMass.masses.map {
                it.rotateAndMove(
                    center,
                    shiftPosition,
                    sin,
                    cos
                )
            })

    }

    /**
     * F = k * m1 * m2 / R^2
     *
     */
    private fun force(firstMass: Mass<P, A>, secondMass: Mass<P, A>): P {
        val massMultiplication = firstMass.attraction.correlation(secondMass.attraction)
        val div = secondMass.point - firstMass.point
        val len = div.length()
        val dir = div / len
        // dir  * m1 * m2 * k / len^2
        return (massMultiplication * forceK / (len * len)).getForceFromPointToPoint(dir)
    }


    private fun mapToMasses(catalog: List<Block<P, A>>): List<MassObject<P, A>> {
        return catalog.map { mapToMass(it.graphs, it.blockName) }
    }

    fun mapToMass(graphs: List<Graph<P, A>>, blockName: String = ""): MassObject<P, A> {
        return when (weightFunction) {
            WeightFunction.OpenContour -> openContourWeight(graphs, blockName)
            else -> throw UnsupportedOperationException("Not support weight function $weightFunction")
        }
    }

    private fun openContourWeight(graphs: List<Graph<P, A>>, blockName: String): MassObject<P, A> {
        //for fragmentation  = 1

        val masses = graphs
            .asSequence()
            .map { it.fragment(fragmentation) }
            .flatMap { it }
            .map { Mass(it.middle(), it.attractionValue()) }
            .toList()
//        val value = masses.first().attraction
//        val firstPoint = value.weightedPosition(masses.first().point)
//
//        //val totalMass = masses.map { it.value }.reduce { acc: A, it: A -> acc.add(it) }
//        val totalMass = masses.map { it.attraction }.reduce { acc: A, it: A -> acc.add(it) }
//        val weightedPoint = masses.stream()
//            .skip(1)
//            .map { it.attraction.weightedPosition(it.point) }
//            .collect(
//                { firstPoint },
//                { left, right -> left.add(right) },
//                { left, right -> left.add(right) })
//        val middlePoint = weightedPoint.div(totalMass.absoluteValue())
        return MassObject(blockName, masses)

    }


}

enum class WeightFunction {
    OpenContour,
    ClosedContour//Площадь замкнутого контура для плоских фигур, объем для 3х мерных фигур
}