package logikcull.loadfiles.loader

import logikcull.loadfiles.LoadFileEntry
import logikcull.loadfiles.exception.InvalidSizeException
import logikcull.loadfiles.parser.LfpLoadFileParser
import logikcull.loadfiles.reader.CsvReader
import logikcull.loadfiles.validator.ControlNumberValidator
import org.junit.Test
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LfpLoaderFileParserTest {

    @Test
    fun validResults() {
        val file = javaClass.getResource("/test.lfp").path.removePrefix("file:")
        val parser = LfpLoadFileParser(CsvReader(file), listOf(ControlNumberValidator(Regex(".+"))))

        val results = parser.use{ it.parse() }
        assertEquals(3, results.size)
        val expected = listOf(
                LoadFileEntry("test-000001", "Import Test 01", "IMAGES/001/test-000001.tif"),
                LoadFileEntry("test-000002", "Import Test 01", "IMAGES/001/test-000002.tif"),
                LoadFileEntry("test-000003", "Import Test 01", "IMAGES/001/test-000003.tif")
        )
        for(index in 0..2) {
            assertEquals(expected[index], results[index])
        }
    }

    @Test
    fun invalidNumberOfFields() {
        val temp = File.createTempFile("bad-data", ".lfp")
        val writer = BufferedWriter(FileWriter(temp))

        writer.append("""
            IM,test-000001,S,0,@Import Test 01;IMAGES 001/;test-000001.tif;2
        """.trimIndent())
        writer.close()

        val parser = LfpLoadFileParser(CsvReader(temp.absolutePath))

        assertFailsWith(InvalidSizeException::class, "Invalid size. Expected 6") {
            parser.parse()
        }
    }

    @Test
    fun invalidNumberOfVolumeAndFileFields() {
        val temp = File.createTempFile("bad-data", ".lfp")
        val writer = BufferedWriter(FileWriter(temp))

        writer.append("""
            IM,test-000001,S,0,@Import Test 01;IMAGES 001/;test-000001.tif;2;junk,0
        """.trimIndent())
        writer.close()

        val parser = LfpLoadFileParser(CsvReader(temp.absolutePath))

        assertFailsWith(InvalidSizeException::class, "Invalid size. Expected 4") {
            parser.parse()
        }
    }

    @Test
    fun nonExistentFile() {
        assertFailsWith(java.nio.file.NoSuchFileException::class) {
            LfpLoadFileParser(CsvReader("/path/to/nowhere")).parse()
        }
    }
}
