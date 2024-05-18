package org.wentura.franko

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExtractGoogleProfilePicture {

    @Test
    fun `Extraction is correct`() {
        val actualUrl =
            "https://lh3.googleusercontent.com/a-/EUFHi8FHsxwHVSeTNgXg9sBc7rQxh6GfPaGrFVYYR8YE7Ds=s96-c"
        val expectedUrl =
            "https://lh3.googleusercontent.com/a-/EUFHi8FHsxwHVSeTNgXg9sBc7rQxh6GfPaGrFVYYR8YE7Ds"

        assertEquals(Utilities.extractGoogleProfilePicture(actualUrl), expectedUrl)
    }
}
