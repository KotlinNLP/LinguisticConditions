/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticdescription.morphology.SingleMorphology
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies the morphological agreement of a token with its governor.
 *
 * @param number whether to check the agreement of the 'number' property of the morphology
 * @param person whether to check the agreement of the 'person' property of the morphology
 * @param case whether to check the agreement of the 'grammatical case' property of the morphology
 * @param degree whether to check the agreement of the 'degree' property of the morphology
 * @param mood whether to check the agreement of the 'mood' property of the morphology
 * @param tense whether to check the agreement of the 'tense' property of the morphology
 */
class MorphologyAgree(
  private val gender: Boolean = false,
  private val number: Boolean = false,
  private val person: Boolean = false,
  private val case: Boolean = false,
  private val degree: Boolean = false,
  private val mood: Boolean = false,
  private val tense: Boolean = false
) : Condition() {

  /**
   * Build a [MorphologyAgree] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents a [MorphologyAgree] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject): this(
    gender = jsonObject.array<String>("properties")!!.contains("gender"),
    number = jsonObject.array<String>("properties")!!.contains("number"),
    person = jsonObject.array<String>("properties")!!.contains("person"),
    case = jsonObject.array<String>("properties")!!.contains("case"),
    degree = jsonObject.array<String>("properties")!!.contains("degree"),
    mood = jsonObject.array<String>("properties")!!.contains("mood"),
    tense = jsonObject.array<String>("properties")!!.contains("tense")
  )

  /**
   * The type of condition.
   */
  override val type: String = "morphology-agree"

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

    val dependentMorpho: SingleMorphology = token.morphologies.single().value
    val governorMorpho: SingleMorphology = governor.morphologies.single().value

    return this.agree(dependentMorpho, governorMorpho)
  }

  /**
   * @param dependentMorpho the dependent morphology
   * @param governorMorpho the governor morphology
   *
   * @return true if the dependent agrees with the governor regarding the properties defined in this condition,
   *         otherwise false
   */
  private fun agree(dependentMorpho: SingleMorphology, governorMorpho: SingleMorphology): Boolean {

    if (gender && !dependentMorpho.agreeInGender(governorMorpho)) return false
    if (number && !dependentMorpho.agreeInNumber(governorMorpho)) return false
    if (person && !dependentMorpho.agreeInPerson(governorMorpho)) return false
    if (case && !dependentMorpho.agreeInCase(governorMorpho)) return false
    if (degree && !dependentMorpho.agreeInDegree(governorMorpho)) return false
    if (mood && !dependentMorpho.agreeInMood(governorMorpho)) return false
    if (tense && !dependentMorpho.agreeInTense(governorMorpho)) return false

    return true
  }
}
