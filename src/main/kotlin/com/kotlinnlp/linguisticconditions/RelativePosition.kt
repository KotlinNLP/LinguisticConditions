/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies the relative position of a token with its governor.
 *
 * @param positionType the position type (right or left)
 */
class RelativePosition(private val positionType: Type) : Condition() {

  /**
   * The position type.
   */
  enum class Type { Top, Right, Left }

  /**
   * The type of condition.
   */
  override val type: String = "relative-position"

  /**
   * Build a [RelativePosition] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents a [RelativePosition] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject) : this(Type.valueOf(jsonObject.string("type")!!))

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

    val headId: Int? = dependencyTree.getHead(token.id)

    return when (this.positionType) {
      Type.Top -> headId == null
      Type.Left -> headId != null && dependencyTree.getPosition(token.id) < dependencyTree.getPosition(headId)
      Type.Right -> headId != null && dependencyTree.getPosition(token.id) > dependencyTree.getPosition(headId)
    }
  }
}
