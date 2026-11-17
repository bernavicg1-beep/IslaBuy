package com.islabuy.camiguin.data.repository

import org.junit.Assert.*
import org.junit.Test

class CloudinaryRepositoryTest {

    @Test
    fun `max photos is 5`() {
        assertEquals(5, CloudinaryRepository.MAX_PHOTOS)
    }

    @Test
    fun `max dimension is 1600`() {
        assertEquals(1600, CloudinaryRepository.MAX_DIMENSION)
    }

    @Test
    fun `jpeg quality is 80`() {
        assertEquals(80, CloudinaryRepository.JPEG_QUALITY)
    }

    @Test
    fun `preset name is correct`() {
        assertEquals("islabuy_products", CloudinaryRepository.PRESET)
    }

    @Test
    fun `folder path is correct`() {
        assertEquals("islabuy/products", CloudinaryRepository.FOLDER)
    }
}
