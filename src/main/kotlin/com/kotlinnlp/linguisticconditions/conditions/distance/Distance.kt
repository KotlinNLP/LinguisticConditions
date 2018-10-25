/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.conditions.distance

import com.kotlinnlp.dependencytree.DependencyTree
import kotlin.math.abs

/**
 * Verify the distance between two tokens.
 */
internal interface Distance {

  /**
   * The value of the distance to verify.
   */
  val value: Int

  /**
   * @param targetId the id of the target token
   * @param refId the id of the reference token (can be null if it represents the root)
   * @param dependencyTree the dependency tree of the tokens sentence
   *
   * @return true if the distance of the target token from the reference is equal to the defined [value]
   */
  fun isVerified(targetId: Int, refId: Int?, dependencyTree: DependencyTree): Boolean =
    refId != null && abs(dependencyTree.getPosition(targetId) - dependencyTree.getPosition(refId)) == this.value
}
