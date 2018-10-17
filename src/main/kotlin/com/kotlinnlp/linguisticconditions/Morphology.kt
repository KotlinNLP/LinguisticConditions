/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticdescription.morphology.properties.*
import com.kotlinnlp.linguisticdescription.morphology.properties.Number
import com.kotlinnlp.linguisticdescription.morphology.properties.interfaces.*
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies the morphology of a token.
 *
 * @param lemma the 'lemma' property of the morphology
 * @param gender the 'gender' property of the morphology
 * @param number the 'number' property of the morphology
 * @param person the 'person' property of the morphology
 * @param case the 'grammatical case' property of the morphology
 * @param degree the 'degree' property of the morphology
 * @param mood the 'mood' property of the morphology
 * @param tense the 'tense' property of the morphology
 */
class Morphology(
  private val lemma: String? = null,
  private val gender: Gender? = null,
  private val number: Number? = null,
  private val person: Person? = null,
  private val case: GrammaticalCase? = null,
  private val degree: Degree? = null,
  private val mood: Mood? = null,
  private val tense: Tense? = null
) : Condition() {

  companion object {

    /**
     * Read a morphology property from a JSON object.
     *
     * @param name the name of the property
     * @param jsonObject the JSON object from which to read the property
     *
     * @return the property read or null if not present
     */
    private fun readProperty(name: String, jsonObject: JsonObject): MorphologyProperty? =
      jsonObject.string(name)?.let { MorphologyPropertyFactory(propertyName = name, valueAnnotation = it) }
  }

  /**
   * Build a [Morphology] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents a [Morphology] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject): this(
    lemma = jsonObject.string("lemma"),
    gender = readProperty("gender", jsonObject) as? Gender,
    number = readProperty("number", jsonObject) as? Number,
    person = readProperty("person", jsonObject) as? Person,
    case = readProperty("case", jsonObject) as? GrammaticalCase,
    degree = readProperty("degree", jsonObject) as? Degree,
    mood = readProperty("mood", jsonObject) as? Mood,
    tense = readProperty("tense", jsonObject) as? Tense
  )

  /**
   * The type of condition.
   */
  override val type: String = "morphology"

  /**
   * @param token a token or null if called on the virtual root
   * @param tokens the list of all the tokens that compose the sentence
   * @param dependencyTree the dependency tree of the token sentence
   *
   * @return a boolean indicating if this condition is verified for the given [token]
   */
  override fun isVerified(token: MorphoSynToken?,
                          tokens: List<MorphoSynToken>,
                          dependencyTree: DependencyTree): Boolean =
    token != null && token.flatMorphologies.first().let { morpho ->
      this.lemma.let { it == null || it == morpho.lemma } &&
        when (morpho) {
          is Genderable -> this.gender.let { it == null || it == morpho.gender }
          is Numerable -> this.number.let { it == null || it == morpho.number }
          is PersonDeclinable -> this.person.let { it == null || it == morpho.person }
          is CaseDeclinable -> this.case.let { it == null || it == morpho.case }
          is Gradable -> this.degree.let { it == null || it == morpho.degree }
          is Conjugable ->
            this.mood.let { it == null || it == morpho.mood } && this.tense.let { it == null || it == morpho.tense }
          else -> throw RuntimeException("Invalid token morphology.")
        }
    }
}
