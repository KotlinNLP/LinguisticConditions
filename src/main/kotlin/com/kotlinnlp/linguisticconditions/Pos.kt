/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticdescription.POSTag
import com.kotlinnlp.linguisticdescription.morphology.POS
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies the part-of-speech (POS) of a token.
 *
 * @property value the POS to be verified
 */
class Pos(val value: POS) : Condition() {

  /**
   * The type of condition.
   */
  override val type: String = "pos"

  /**
   * Build a [Pos] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents a [Pos] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject): this(POS.byAnnotation(jsonObject.string("value")!!))

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
    token != null && (token.pos as POSTag.Base).type == this.value
}
