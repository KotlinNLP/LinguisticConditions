/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.operators

import com.kotlinnlp.linguisticconditions.Condition
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The 'all-direct-descendants' operator.
 * It verifies that all the direct descendants of a given token that verify the [condition].
 *
 * @param condition the condition to which this operator is applied
 */
class AllDirectDescendants(condition: Condition) : Operator.Single(condition) {

  companion object {

    /**
     * The annotation of the condition.
     */
    const val ANNOTATION: String = "all-direct-descendants"
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

    val descendantsIds: List<Int> = if (token != null)
      dependencyTree.getDependents(token.id)
    else
      dependencyTree.getRoots() // the token is the virtual root and its direct dependents are all the roots

    return descendantsIds.all {
      this.condition.isVerified(
        token = tokens[dependencyTree.getPosition(it)],
        tokens = tokens,
        dependencyTree = dependencyTree)
    }
  }
}
