/**
 * A Java library to handle success and failure without exceptions
 * <h2>Lazy Result Library for Java</h2>
 * <p>
 * Lazy results can be manipulated just like any other result; they will try to defer the invocation of the given
 * supplier as long as possible. The purpose is to encapsulate an expensive operation that may be omitted if there's no
 * actual need to examine the result.
 *
 * @author Guillermo Calvo
 * @see com.leakyabstractions.result
 * @see com.leakyabstractions.result.jackson.ResultModule
 */

package com.leakyabstractions.result.jackson;
