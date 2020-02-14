package logikcull.loadfiles.parser

import logikcull.loadfiles.LoadFileEntry
import logikcull.loadfiles.validator.LoadFileResultValidator

/**
 * The LoadFile is the base class that all file loaders should start from. It handles validation, parsing
 * and returning the resulting list of LoadFileEntry.
 *
 * @param postValidators The validators that validate any conditions needed from the resulting data of the load file
 */
abstract class LoadFileParser(private val postValidators: List<LoadFileResultValidator>): AutoCloseable {

    fun parse(): List<LoadFileEntry> {
        val results = parseLoad()
        validatePostResults(results)
        return results
    }

    protected open fun parseLoad(): List<LoadFileEntry> {
        throw NotImplementedError("Method not implemented")
    }

    private fun validatePostResults(results: List<LoadFileEntry>) {
        results.forEach { loadFileEntry ->
            postValidators.forEach { validator ->
                val result = validator.validate(loadFileEntry)
                if (!result.isValid) {
                    throw IllegalArgumentException(result.errorMessage)
                }
            }
        }
    }
}