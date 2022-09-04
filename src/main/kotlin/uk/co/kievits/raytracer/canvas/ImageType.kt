package uk.co.kievits.raytracer.canvas

sealed class ImageType<C : Canvas>(
    builder: (height: Int, width: Int) -> C
) : (Int, Int) -> C by builder {
    object PPM : ImageType<PpmCanvas>(::PpmCanvas)
    object PNG : ImageType<BitmapCanvas>(::BitmapCanvas)
}
