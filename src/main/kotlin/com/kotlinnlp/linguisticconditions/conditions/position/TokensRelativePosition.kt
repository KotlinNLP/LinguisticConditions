/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.conditions.position

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticconditions.conditions.DoubleCondition
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies the relative position of two tokens.
 *
 * @param positionType the position type to verify
 */
internal class TokensRelativePosition(
  override val positionType: RelativePosition.Type
) : RelativePosition, DoubleCondition() {

  companion object {

    /**
     * The annotation of the condition.
     */
    const val ANNOTATION: String = "tokens-relative-position"
  }

  /**
   * Build a [TokensRelativePosition] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents a [TokensRelativePosition] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject) : this(RelativePosition.Type.valueOf(jsonObject.string("type")!!))

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
    this.isVerified(targetId = tokenA.id, refId = tokenB.id, dependencyTree = dependencyTree)
}
