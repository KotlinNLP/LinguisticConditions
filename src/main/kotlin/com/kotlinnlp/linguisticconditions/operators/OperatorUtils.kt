/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.operators

import com.kotlinnlp.dependencytree.DependencyTree

/**
 * @param tokenId the id of a token
 * @param dependencyTree the dependency tree that the token is part of
 *
 * @return the list of ancestors ids of the given token
 */
internal fun getAncestorsIds(tokenId: Int, dependencyTree: DependencyTree): List<Int> {

  val ancestorsIds: MutableList<Int> = mutableListOf()
  var headId: Int? = dependencyTree.getHead(tokenId)

  while (headId != null) {
    ancestorsIds.add(headId)
    headId = dependencyTree.getHead(tokenId)
  }

  return ancestorsIds
}
