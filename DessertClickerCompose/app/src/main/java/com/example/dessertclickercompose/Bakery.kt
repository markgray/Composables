package com.example.dessertclickercompose

/**
 * Our Singleton instance of [Bakery]
 */
val bakery: Bakery = Bakery()

/**
 * Simple data class that represents a dessert. Includes the resource id integer associated with
 * the image, the price it's sold for, and the startProductionAmount, which determines when
 * the dessert starts to be produced.
 */
data class Dessert(
    /**
     * Resource id for the image of the dessert
     */
    val imageId: Int,
    /**
     * Price of the dessert
     */
    val price: Int,
    /**
     * Number of desserts sold when we start to produce more expensive desserts. We search thru
     * the list until we see a dessert who's "startProductionAmount" is greater than the amount
     * sold and then switch to that dessert.
     */
    val startProductionAmount: Int
)

/**
 * This class holds the state of our dessert sales.
 */
class Bakery {
    /**
     * Create a list of all desserts, in order of when they start being produced
     */
    private val allDesserts = listOf(
        Dessert(R.drawable.cupcake, 5, 0),
        Dessert(R.drawable.donut, 10, 5),
        Dessert(R.drawable.eclair, 15, 20),
        Dessert(R.drawable.froyo, 30, 50),
        Dessert(R.drawable.gingerbread, 50, 100),
        Dessert(R.drawable.honeycomb, 100, 200),
        Dessert(R.drawable.icecreamsandwich, 500, 500),
        Dessert(R.drawable.jellybean, 1000, 1000),
        Dessert(R.drawable.kitkat, 2000, 2000),
        Dessert(R.drawable.lollipop, 3000, 4000),
        Dessert(R.drawable.marshmallow, 4000, 8000),
        Dessert(R.drawable.nougat, 5000, 16000),
        Dessert(R.drawable.oreo, 6000, 20000)
    )

    /**
     * The current [Dessert] we are selling (and displaying in our UI).
     */
    var currentDessert: Dessert = allDesserts[0]

    /**
     * Running total of the price of all desserts sold so far.
     */
    var revenue: Int = 0

    /**
     * Total number of desserts sold so far.
     */
    var dessertsSold: Int = 0

    /**
     * Updates the score when the dessert is clicked. Possibly shows a new dessert. We add the
     * `price` field of our [Dessert] field [currentDessert] to our field [revenue], and increment
     * our [dessertsSold] field.
     */
    fun onDessertClicked() {
        // Update the score
        revenue += currentDessert.price
        dessertsSold++
        var newDessert = allDesserts[0]
        for (dessert in allDesserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                newDessert = dessert
            }
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }
        // If the new dessert is actually different than the current dessert, update the image
        if (newDessert != currentDessert) {
            currentDessert = newDessert
        }

    }

}