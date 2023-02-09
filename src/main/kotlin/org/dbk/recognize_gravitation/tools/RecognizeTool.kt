package org.dbk.recognize_gravitation.tools


class RecognizeTool<P: IPoint<P>, A: AttractionValue<A>>(
    val catalog: List<Block<P,A>>,
    val k: Double,// коэффициент усиления
    val distanceDegree: Double,//степень расстояния
    private val weightFunction: WeightFunction,//функция веса может быть скаляром, может быть вектором
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
    fun recognize(graphs: List<Graph<P,A>>): Pair<Double, String>? {
        val mass = mapToMass(graphs)

        val results = masses.map { Pair(findNearestPosition(it, mass), it.name) }
        return results.maxByOrNull { it.first }
    }

    fun findNearestPosition(first: MassObject<P,A>, second: MassObject<P,A>): Double {
        val dir = second.point.subtract(first.point)

        val resultantForcesByItems = first.masses.map { firstMass ->
            val resultantForce = second.masses.map { secondMass -> force(firstMass, secondMass) }
                .reduce { acc, p -> acc.add(p) }
            Pair(firstMass, resultantForce)
        }
        var resultantForce: P = resultantForcesByItems.first().second
        val firstCenter = first.point

        val firstMoment: Double
        resultantForcesByItems.asSequence().drop(1).forEach {
            resultantForce = resultantForce.add(it.second)


        }


    }

    /**
     * F = k * m1 * m2 / R^2
     *
     */
    private fun force(firstMass: Mass<P, A>, secondMass: Mass<P, A>): P {
        val massMultiplication = firstMass.attraction.correlation(secondMass.attraction)
        val div = firstMass.point.subtract(secondMass.point)
        val len = div.length()
        val dir = div.div(len)
         (massMultiplication / (len * len)) * k
    }


    private fun mapToMasses(catalog: List<Block<P,A>>): List<MassObject<P,A>> {
        return catalog.map { mapToMass(it.graphs, it.blockName) }
    }

    private fun mapToMass(graphs: List<Graph<P,A>>, blockName: String = ""): MassObject<P,A> {
        return when (weightFunction) {
            WeightFunction.OpenContour -> openContourWeight(graphs, blockName)
            else -> throw UnsupportedOperationException("Not support weight function $weightFunction")
        }
    }

    private fun openContourWeight(graphs: List<Graph<P,A>>, blockName: String): MassObject<P,A> {
        //for fragmentation  = 1

        val masses = graphs
            .asSequence()
            .map { it.fragment(fragmentation) }
            .flatMap { it }
            .map { Mass(it.middle(), it.attractionValue()) }
            .toList()
        val value = masses.first().attraction
        val firstPoint = value.weightedPosition(masses.first().point)

        //val totalMass = masses.map { it.value }.reduce { acc: A, it: A -> acc.add(it) }
        val totalMass = masses.map { it.attraction }.reduce { acc: A, it: A -> acc.add(it) }
        val weightedPoint = masses.stream()
            .skip(1)
            .map { it.attraction.weightedPosition(it.point) }
            .collect(
                { firstPoint },
                { left, right -> left.add(right) },
                { left, right -> left.add(right) })
        val middlePoint = weightedPoint.div(totalMass.absoluteValue())
        return MassObject(blockName, middlePoint, totalMass, masses)

    }


}

enum class WeightFunction {
    OpenContour,
    ClosedContour//Площадь замкнутого контура для плоских фигур, объем для 3х мерных фигур
}