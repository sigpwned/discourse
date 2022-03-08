package com.sigpwned.discourse.core.util;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks generated code to exclude from code coverage
 */
@Retention(CLASS)
@Target({TYPE, METHOD})
public @interface Generated {
}
