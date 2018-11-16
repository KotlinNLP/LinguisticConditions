/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.conditions.morpho

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticconditions.Condition
import com.kotlinnlp.linguisticdescription.morphology.POS
import com.kotlinnlp.linguisticdescription.morphology.SingleMorphology
import com.kotlinnlp.linguisticdescription.morphology.properties.*
import com.kotlinnlp.linguisticdescription.morphology.properties.Number
import com.kotlinnlp.linguisticdescription.morphology.properties.interfaces.*
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies the morphology of a token.
 *
 * @param checkContext whether to verify this condition on the context morphology instead of the morphology
 * @param lemma the 'lemma' property of the morphology
 * @param pos the 'pos' property of the morphology
 * @param posPartial the partial 'pos' property of the morphology
 * @param number the 'number' property of the morphology
 * @param person the 'person' property of the morphology
 * @param case the 'grammatical case' property of the morphology
 * @param degree the 'degree' property of the morphology
 * @param mood the 'mood' property of the morphology
 * @param tense the 'tense' property of the morphology
 */
internal class Morphology(
  override val checkContext: Boolean = false,
  private val lemma: String? = null,
  private val pos: POS? = null,
  private val posPartial: POS? = null,
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
     * The annotation of the condition.
     */
    const val ANNOTATION: String = "morphology"

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
    checkContext = jsonObject.boolean("context") ?: false,
    lemma = jsonObject.string("lemma"),
    pos = jsonObject.string("pos")?.let { POS.byAnnotation(it) },
    posPartial = jsonObject.string("pos-partial")?.let { POS.byAnnotation(it) },
    gender = readProperty("gender", jsonObject) as? Gender,
    number = readProperty("number", jsonObject) as? Number,
    person = readProperty("person", jsonObject) as? Person,
    case = readProperty("case", jsonObject) as? GrammaticalCase,
    degree = readProperty("degree", jsonObject) as? Degree,
    mood = readProperty("mood", jsonObject) as? Mood,
    tense = readProperty("tense", jsonObject) as? Tense
  )

  /**
   * Whether this condition looks at a single token, without requiring to check other tokens properties.
   */
  override val isUnary: Boolean = true

  /**
   * Whether this condition looks at a dependent-governor tokens pair, without requiring to check other tokens
   * properties.
   */
  override val isBinary: Boolean = false

  /**
   * Whether this condition needs to look at the morphology.
   */
  override val checkMorpho: Boolean = true

  /**
   * Whether this condition needs to look at the morphological properties.
   */
  override val checkMorphoProp: Boolean =
    sequenceOf(gender, number, person, case, degree, mood, tense).any { it != null }

  /**
   * Check requirements.
   */
  init {
    require(!this.checkContext || this.checkMorphoProp) {
      "The 'checkContext' property cannot be true if the condition does not check morphological properties."
    }
  }

  /**
   * @param token a token or null if called on the virtual root
   * @param tokens the list of all the tokens that compose the sentence
   * @param dependencyTree the dependency tree of the token sentence
   *
   * @return a boolean indicating if this condition is verified for the given [token]
   */
  override fun isVerified(token: MorphoSynToken.Single?,
                          tokens: List<MorphoSynToken.Single>,
                          dependencyTree: DependencyTree): Boolean {

    if (token == null) return false

    val morpho: SingleMorphology = this.getMorphology(token)

    return this.lemma?.equals(morpho.lemma) ?: true &&
      this.pos?.equals(morpho.pos) ?: true &&
      this.posPartial?.let { morpho.pos.isComposedBy(it) } ?: true &&
      this.gender?.let { morpho is Genderable && it == morpho.gender } ?: true &&
      this.number?.let { morpho is Numerable && it == morpho.number } ?: true &&
      this.person?.let { morpho is PersonDeclinable && it == morpho.person } ?: true &&
      this.case?.let { morpho is CaseDeclinable && it == morpho.case } ?: true &&
      this.degree?.let { morpho is Gradable && it == morpho.degree } ?: true &&
      this.mood?.let { morpho is Conjugable && it == morpho.mood } ?: true &&
      this.tense?.let { morpho is Conjugable && it == morpho.tense } ?: true
  }
}
