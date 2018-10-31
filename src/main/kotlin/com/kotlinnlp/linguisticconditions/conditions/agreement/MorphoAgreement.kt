/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.conditions.agreement

import com.kotlinnlp.linguisticdescription.morphology.SingleMorphology
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * Verify the morphological agreement between two morphologies.
 */
internal interface MorphoAgreement {

  /**
   * Whether to check the agreement looking at the context morphology.
   */
  val checkContext: Boolean

  /**
   * Whether to check the agreement of the 'lemma' property of the morphology.
   */
  val lemma: Boolean

  /**
   * Whether to check the agreement of the 'pos' property of the morphology.
   */
  val pos: Boolean

  /**
   * Whether to check the agreement of the 'gender' property of the morphology.
   */
  val gender: Boolean

  /**
   * Whether to check the agreement of the 'number' property of the morphology.
   */
  val number: Boolean

  /**
   * Whether to check the agreement of the 'person' property of the morphology.
   */
  val person: Boolean

  /**
   * Whether to check the agreement of the 'grammatical case' property of the morphology.
   */
  val case: Boolean

  /**
   * Whether to check the agreement of the 'degree' property of the morphology.
   */
  val degree: Boolean

  /**
   * Whether to check the agreement of the 'mood' property of the morphology.
   */
  val mood: Boolean

  /**
   * Whether to check the agreement of the 'tense' property of the morphology.
   */
  val tense: Boolean

  /**
   * @param tokenA a single morpho-syntactic token
   * @param tokenB a single morpho-syntactic token
   *
   * @return true if the given morphologies agree regarding the properties enabled, otherwise false
   */
  fun isVerified(tokenA: MorphoSynToken.Single, tokenB: MorphoSynToken.Single): Boolean {

    val morphoA: SingleMorphology = this.getMorphology(tokenA)
    val morphoB: SingleMorphology = this.getMorphology(tokenB)

    if (lemma && morphoA.lemma != morphoB.lemma) return false
    if (pos && morphoA.pos != morphoB.pos) return false
    if (gender && !morphoA.agreeInGender(morphoB)) return false
    if (number && !morphoA.agreeInNumber(morphoB)) return false
    if (person && !morphoA.agreeInPerson(morphoB)) return false
    if (case && !morphoA.agreeInCase(morphoB)) return false
    if (degree && !morphoA.agreeInDegree(morphoB)) return false
    if (mood && !morphoA.agreeInMood(morphoB)) return false
    if (tense && !morphoA.agreeInTense(morphoB)) return false

    return true
  }

  /**
   * @param token a single morpho-syntactic token
   *
   * @return the morphology of the token on which to check the agreement
   */
  private fun getMorphology(token: MorphoSynToken.Single): SingleMorphology =
    if (this.checkContext)
      token.contextMorphologies.single().value
    else
      token.morphologies.single().value
}
