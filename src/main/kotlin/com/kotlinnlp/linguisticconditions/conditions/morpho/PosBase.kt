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
import com.kotlinnlp.linguisticdescription.POSTag
import com.kotlinnlp.linguisticdescription.morphology.POS
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies the part-of-speech (POS) of a token.
 *
 * @property value the POS to be verified
 */
internal class PosBase(val value: POS) : Condition() {

  companion object {

    /**
     * The annotation of the condition.
     */
    const val ANNOTATION: String = "pos-base"
  }

  /**
   * Build a [PosBase] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents a [PosBase] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject): this(POS.byAnnotation(jsonObject.string("value")!!))

  /**
   * Check requirements.
   */
  init {
    require(this.value.components.size == 1) { "The value of the PosBase condition must be a base POS." }
  }

  /**
   * Whether this condition looks at a single token, without requiring to check other tokens properties.
   */
  override val isUnary: Boolean = true

  /**
   * Whether this condition needs to look at the morphological properties.
   */
  override val checkMorpho: Boolean = false

  /**
   * Whether this condition needs to look at the context morphology.
   */
  override val checkContext: Boolean = false

  /**
   * @param token a token or null if called on the virtual root
   * @param tokens the list of all the tokens that compose the sentence
   * @param dependencyTree the dependency tree of the token sentence
   *
   * @return a boolean indicating if this condition is verified for the given [token]
   */
  override fun isVerified(token: MorphoSynToken.Single?,
                          tokens: List<MorphoSynToken.Single>,
                          dependencyTree: DependencyTree): Boolean =
    token != null && (token.pos as POSTag.Base).type.isComposedBy(this.value)
}
