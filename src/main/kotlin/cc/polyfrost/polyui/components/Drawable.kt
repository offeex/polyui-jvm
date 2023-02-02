package cc.polyfrost.polyui.components

import cc.polyfrost.polyui.layouts.Layout
import cc.polyfrost.polyui.renderer.Renderer
import cc.polyfrost.polyui.units.Point
import cc.polyfrost.polyui.units.Size
import cc.polyfrost.polyui.units.Unit
import cc.polyfrost.polyui.units.Vec2
import org.jetbrains.annotations.ApiStatus


/** The most basic component in the PolyUI system. <br>
 * This class is implemented for both [cc.polyfrost.polyui.layouts.Layout] and [Component]. <br>
 */
@ApiStatus.Internal
interface Drawable {
    val at: Point<Unit>
    var sized: Size<Unit>?
    val renderer: Renderer
    var onAdded: (Drawable.() -> kotlin.Unit)?
    var onRemoved: (Drawable.() -> kotlin.Unit)?

    /** reference to the layout encapsulating this drawable.
     * For components, this is never null, but for layouts, it can be null (meaning its parent is the polyui)
     */
    val layout: Layout?

    /** pre-render functions, such as applying transforms. */
    fun preRender()

    /** draw script for this drawable. */
    fun render()

    /** post-render functions, such as removing transforms. */
    fun postRender()

    /** calculate the position and size of this drawable. Make sure to call [doDynamicSize] in this method to avoid issues with sizing.
     *
     * This method is called once the [layout] is populated for children and components, and when a recalculation is requested.
     *
     * The value of [layout]'s bounds will be updated after this method is called, so **do not** use [layout]'s bounds as an updated value in this method.
     */
    fun calculateBounds()

    /** method that is called when the physical size of the total window area changes. */
    fun rescale(scaleX: Float, scaleY: Float) {
        at.scale(scaleX, scaleY)
        sized!!.scale(scaleX, scaleY)
    }

    /** function that should return true if it is ready to be removed from its parent.
     *
     * This is used for components that need to wait for an animation to finish before being removed.
     */
    fun canBeRemoved(): Boolean


    fun debugRender() {
        TODO("Not yet implemented")
    }

    fun x(): Float = at.x()
    fun y(): Float = at.y()
    fun width(): Float = sized?.width()
        ?: throw IllegalStateException("drawable $this has no size, but should have a size initialized by this point")

    fun height(): Float = sized?.height()
        ?: throw IllegalStateException("drawable $this has no size, but should have a size initialized by this point")

    fun isInside(x: Float, y: Float): Boolean {
        return x >= this.x() && x <= this.x() + this.width() && y >= this.y() && y <= this.y() + this.height()
    }

    fun atUnitType(): Unit.Type {
        return at.type()
    }

    fun sizedUnitType(): Unit.Type {
        return sized!!.type()
    }

    fun doDynamicSize() {
        if (sized!!.a is Unit.Dynamic) (sized!!.a as Unit.Dynamic).set(
            layout?.sized?.a ?: throw IllegalStateException("Dynamic units only work on parents with a set size!")
        )
        if (sized!!.b is Unit.Dynamic) (sized!!.b as Unit.Dynamic).set(
            layout?.sized?.b ?: throw IllegalStateException("Dynamic units only work on parents with a set size!")
        )
        if (at.a is Unit.Dynamic) (at.a as Unit.Dynamic).set(
            layout?.sized?.a ?: throw IllegalStateException("Dynamic units only work on parents with a set size!")
        )
        if (at.b is Unit.Dynamic) (at.b as Unit.Dynamic).set(
            layout?.sized?.b ?: throw IllegalStateException("Dynamic units only work on parents with a set size!")
        )
    }

    /** Implement this function to return the size of this drawable, if no size is specified during construction.
     *
     * This should be so that if the component can determine its own size (for example, it is an image), then the size parameter in the constructor can be omitted using:
     *
     * `sized: Vec2<cc.polyfrost.polyui.units.Unit>? = null;` and this method **needs** to be implemented!
     *
     *
     * Otherwise, the size parameter in the constructor must be specified.
     * @throws UnsupportedOperationException if this method is not implemented, and the size parameter in the constructor is not specified. */
    fun getSize(): Vec2<Unit>? {
        return null
    }
}