/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions

import com.kotlinnlp.dependencytree.DependencyTree

/**
 * Verify the relative position of two tokens.
 */
internal interface RelativePosition {

  /**
   * The position type.
   */
  enum class Type { Top, Right, Left }

  /**
   * The position type to verify.
   */
  val positionType: Type

  /**
   * @param tokenId the id of a token
   * @param refId the id of the reference token (can be null if it represents the root)
   * @param dependencyTree the dependency tree of the tokens sentence
   *
   * @return true if the relative position of the given token has the defined [positionType] respect to the reference
   */
  fun isVerified(tokenId: Int, refId: Int?, dependencyTree: DependencyTree): Boolean =
    when (this.positionType) {
      Type.Top -> refId == null
      Type.Left -> refId != null && dependencyTree.getPosition(tokenId) < dependencyTree.getPosition(refId)
      Type.Right -> refId != null && dependencyTree.getPosition(tokenId) > dependencyTree.getPosition(refId)
    }
}
