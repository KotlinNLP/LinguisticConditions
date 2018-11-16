/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.conditions.agreement

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticconditions.Condition
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies the morphological agreement of a token with its governor.
 *
 * @param checkContext whether to check the agreement looking at the context morphology
 * @param lemma whether to check the agreement of the 'lemma' property of the morphology
 * @param pos whether to check the agreement of the 'pos' property of the morphology
 * @param gender whether to check the agreement of the 'gender' property of the morphology
 * @param number whether to check the agreement of the 'number' property of the morphology
 * @param person whether to check the agreement of the 'person' property of the morphology
 * @param case whether to check the agreement of the 'grammatical case' property of the morphology
 * @param degree whether to check the agreement of the 'degree' property of the morphology
 * @param mood whether to check the agreement of the 'mood' property of the morphology
 * @param tense whether to check the agreement of the 'tense' property of the morphology
 */
internal class AgreementWithGovernor(
  override val checkContext: Boolean = false,
  override val lemma: Boolean = false,
  override val pos: Boolean = false,
  override val gender: Boolean = false,
  override val number: Boolean = false,
  override val person: Boolean = false,
  override val case: Boolean = false,
  override val degree: Boolean = false,
  override val mood: Boolean = false,
  override val tense: Boolean = false
) : MorphoAgreement, Condition() {

  companion object {

    /**
     * The annotation of the condition.
     */
    const val ANNOTATION: String = "agreement-with-governor"
  }

  /**
   * Build an [AgreementWithGovernor] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents an [AgreementWithGovernor] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject): this(
    checkContext = jsonObject.boolean("context") ?: false,
    lemma = jsonObject.array<String>("properties")!!.contains("lemma"),
    pos = jsonObject.array<String>("properties")!!.contains("pos"),
    gender = jsonObject.array<String>("properties")!!.contains("gender"),
    number = jsonObject.array<String>("properties")!!.contains("number"),
    person = jsonObject.array<String>("properties")!!.contains("person"),
    case = jsonObject.array<String>("properties")!!.contains("case"),
    degree = jsonObject.array<String>("properties")!!.contains("degree"),
    mood = jsonObject.array<String>("properties")!!.contains("mood"),
    tense = jsonObject.array<String>("properties")!!.contains("tense")
  )

  /**
   * Whether this condition looks at a single token, without requiring to check other tokens properties.
   */
  override val isUnary: Boolean = false

  /**
   * Whether this condition needs to look at the morphology.
   */
  override val checkMorpho: Boolean = true

  /**
   * Whether this condition needs to look at the morphological properties.
   */
  override val checkMorphoProp: Boolean = sequenceOf(gender, number, person, case, degree, mood, tense).any()

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

    val governorId: Int = dependencyTree.getHead(token.id) ?: return false
    val governor: MorphoSynToken.Single = tokens[dependencyTree.getPosition(governorId)]

    return this.isVerified(token, governor)
  }
}
