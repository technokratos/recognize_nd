package org.dbk.recognize_gravitation.tools

import kotlin.math.*


class RecognizeTool<P : IPoint<P>, A : AttractionValue<A>>(
    private val catalog: List<Block<P, A>>,
    private val forceK: Double = 1.0,// коэффициент усиления
    val distanceDegree: Double = 2.0,//степень расстояния
    private val angleK: Double = 1.0,
    private val shiftK: Double = 1.0,
    private val weightFunction: WeightFunction = WeightFunction.OpenContour,//функция веса может быть скаляром, может быть вектором
    private val proximityLevel: Double = 0.01,
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

        val results = masses.map { Pair(findMaxForceByRotation(it, mass), it.name) }
        return results.maxByOrNull { it.first }
    }


    fun findMaxForceByRotation(mass: MassObject<P, A>, baseMass: MassObject<P, A>): Double {
        val baseDeltaAngle = Math.PI / 180 * 2
        val lambda = 0.1

        var angle = 0.0

        var deltaAngle = baseDeltaAngle

        val center = baseMass.point
        val shiftPosition = center.subtract(mass.point)


        var mass0 = mass.rotateAndMove(center, shiftPosition, angle)
        var prevF = getResultantForce(mass0, baseMass)
        angle += deltaAngle


        var force: Double = 0.0
        val multiplicationMass = mass.value.absoluteValue() * baseMass.value.absoluteValue()
        var maxForce : Double = prevF
        do {
            val mass1 = mass0.rotateAndMove(center, center.zero(), angle)
            val nextF = getResultantForce(mass1, baseMass)
            //if center instance of P2
            //rotation axis is 0Z - axis
            val deltaF = nextF - prevF
            val deltaAngle = baseDeltaAngle * sign(deltaF)//согласовать масштаб силы и с масштабом произведения масс
            angle += deltaAngle
            prevF = nextF
            force = nextF
            if (nextF > maxForce) {
                maxForce = nextF
            }
        } while (abs(maxForce - prevF) > proximityLevel)

        return force / (multiplicationMass)
    }
    fun findMaxForceByGradient(mass: MassObject<P, A>, baseMass: MassObject<P, A>): Double {
        val baseDeltaAngle = Math.PI / 180 * 2
        val lambda = 0.1

        var angle = 0.0

        var deltaAngle = baseDeltaAngle

        val center = baseMass.point
        val shiftPosition = center.subtract(mass.point)


        var leftMass = mass.rotateAndMove(center, shiftPosition, angle - deltaAngle)
        var rightMass = mass.rotateAndMove(center, shiftPosition, angle + deltaAngle)
        var leftForce = getResultantForce(rightMass, baseMass)
        var force = getResultantForce(mass, baseMass)
        var rightForce = getResultantForce(rightMass, baseMass)

        var dir = rightForce > leftForce

        




        val multiplicationMass = mass.value.absoluteValue() * baseMass.value.absoluteValue()
        var maxForce : Double = prevF
        do {
            val mass1 = rightMass.rotateAndMove(center, center.zero(), angle)
            val nextF = getResultantForce(mass1, baseMass)
            //if center instance of P2
            //rotation axis is 0Z - axis
            val deltaF = nextF - prevF
            val deltaAngle = baseDeltaAngle * sign(deltaF)//согласовать масштаб силы и с масштабом произведения масс
            angle += deltaAngle
            prevF = nextF
            force = nextF
            if (nextF > maxForce) {
                maxForce = nextF
            }
        } while (abs(maxForce - prevF) > proximityLevel)

        return force / (multiplicationMass)
    }

    private fun getResultantForce(
        firstMass: MassObject<P, A>,
        baseMass: MassObject<P, A>
    ): Double {
        val resultantForcesByItems = firstMass.masses
            .asSequence()
            .map { firstItem ->
                val resultantForceByItem = baseMass.masses.asSequence()
                    .map { anotherItem -> force(firstItem, anotherItem) }
                    .reduce { acc, p -> acc + p }
                Pair(firstItem, resultantForceByItem)
            }
        //todo merge two streams
        return resultantForcesByItems
            .map { it.second }
            .reduce { acc, p -> acc + p }
    }

//    private fun getMoment(
//        firstMass: MassObject<P, A>,
//        baseMass: MassObject<P, A>
//    ): Double {
//        val resultantForcesByItems = firstMass.masses
//            .map { firstItem ->
//                val resultantForceByItem = baseMass.masses.asSequence()
//                    .map { anotherItem -> force(firstItem, anotherItem) }
//                    .reduce { acc, p -> acc + p }
//                Pair(firstItem, resultantForceByItem)
//            }
//        val resultantForce = resultantForcesByItems.asSequence()
//            .map { it.second }
//            .reduce { acc, p -> acc + p }
//
//        val resultantMoment = resultantForcesByItems.asSequence()
//            .map { pair ->
//                val position = pair.first.point
//                val force = pair.second
//                val forceVector = Vector(position, position.add(force))
//                val perpendicularToLine = firstMass.point.perpendicularToLine(forceVector)
//                val moment = perpendicularToLine.crossProduct(force)
//                moment
//            }.reduce { acc, p3 -> acc + p3 }
//        return Pair(resultantForce, resultantMoment)
//    }

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
//    private fun force(firstMass: Mass<P, A>, secondMass: Mass<P, A>): P {
//        val massMultiplication = firstMass.attraction.correlation(secondMass.attraction)
//        val div = secondMass.point - firstMass.point
//        val len = div.length()
//        if (len != 0.0) { //todo compare with precision
//            val dir = div / len
//            return (massMultiplication * forceK / (len * len)).getForceFromPointToPoint(dir)
//        } else {
//
//        }
//    }
    private fun force(firstMass: Mass<P, A>, secondMass: Mass<P, A>): Double {
        val massMultiplication = firstMass.attraction.correlation(secondMass.attraction)
        val div = secondMass.point - firstMass.point
        val lenSquared = div.lengthSquared()
        val limitedSquaredLen = max(lenSquared, 1.0)
        return massMultiplication.absoluteValue() * forceK / limitedSquaredLen

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