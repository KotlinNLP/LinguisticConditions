/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.conditions.agreement

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticconditions.conditions.DoubleCondition
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies the morphological agreement between two tokens.
 *
 * @param lemma whether to check the agreement of the 'lemma' property of the morphology
 * @param gender whether to check the agreement of the 'gender' property of the morphology
 * @param number whether to check the agreement of the 'number' property of the morphology
 * @param person whether to check the agreement of the 'person' property of the morphology
 * @param case whether to check the agreement of the 'grammatical case' property of the morphology
 * @param degree whether to check the agreement of the 'degree' property of the morphology
 * @param mood whether to check the agreement of the 'mood' property of the morphology
 * @param tense whether to check the agreement of the 'tense' property of the morphology
 */
internal class TokensAgreement(
  override val lemma: Boolean = false,
  override val gender: Boolean = false,
  override val number: Boolean = false,
  override val person: Boolean = false,
  override val case: Boolean = false,
  override val degree: Boolean = false,
  override val mood: Boolean = false,
  override val tense: Boolean = false
) : MorphoAgreement, DoubleCondition() {

  companion object {

    /**
     * The annotation of the condition.
     */
    const val ANNOTATION: String = "tokens-agreement"
  }

  /**
   * Build a [TokensAgreement] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents a [TokensAgreement] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject) : this(
    lemma = jsonObject.array<String>("properties")!!.contains("lemma"),
    gender = jsonObject.array<String>("properties")!!.contains("gender"),
    number = jsonObject.array<String>("properties")!!.contains("number"),
    person = jsonObject.array<String>("properties")!!.contains("person"),
    case = jsonObject.array<String>("properties")!!.contains("case"),
    degree = jsonObject.array<String>("properties")!!.contains("degree"),
    mood = jsonObject.array<String>("properties")!!.contains("mood"),
    tense = jsonObject.array<String>("properties")!!.contains("tense")
  )

  /**
   * @param tokenA a token of the sentence
   * @param tokenB a token of the sentence
   * @param tokens the list of all the tokens that compose the sentence
   * @param dependencyTree the dependency tree of the token sentence
   *
   * @return a boolean indicating if this condition is verified for the two given tokens
   */
  override fun isVerified(tokenA: MorphoSynToken.Single,
                          tokenB: MorphoSynToken.Single,
                          tokens: List<MorphoSynToken.Single>,
                          dependencyTree: DependencyTree): Boolean =
    this.isVerified(morphoA = tokenA.morphologies.single().value, morphoB = tokenB.morphologies.single().value)
}
