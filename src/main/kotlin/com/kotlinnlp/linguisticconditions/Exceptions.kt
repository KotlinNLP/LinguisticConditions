/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions

/**
 * Thrown when the constructor via JSON object is missing in a [Condition].
 *
 * @param conditionType the type of the condition with the missing constructor
 */
class MissingJSONConstructor(conditionType: String) : RuntimeException("'$conditionType' condition")

/**
 * Thrown when a Value is missing in a JSON condition.
 */
class MissingValue : RuntimeException()

/**
 * Thrown when there are too many Values in a JSON condition.
 */
class TooManyValues : RuntimeException()

/**
 * Thrown when the type of a JSON operator is not valid.
 *
 * @param type the type of the operator
 */
class InvalidOperatorType(type: String) : RuntimeException(type)
