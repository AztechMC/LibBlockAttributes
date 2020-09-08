/*
 * Copyright (c) 2019 AlexIIL
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package alexiil.mc.lib.attributes.fluid.filter;

import java.util.function.Predicate;

import alexiil.mc.lib.attributes.fluid.volume.FluidKey;

/** A general {@link Predicate} interface for {@link FluidKey}s.
 * <p>
 * 3 basic implementations are provided:
 * <ul>
 * <li>{@link ConstantFluidFilter} for filters that always match or never match.</li>
 * <li>{@link ExactFluidFilter} for a filter that matches on a single {@link FluidKey}, and rejects others.</li>
 * <li>{@link AggregateFluidFilter} for a filter that either AND's or OR's several other {@link FluidFilter}'s into
 * one.</li>
 * </ul>
 */
@FunctionalInterface
public interface FluidFilter {

    /** Checks to see if the given filter matches the given fluid key.
     * 
     * @throws IllegalArgumentException if the given {@link FluidKey} is {@link FluidKey#isEmpty() empty}. */
    boolean matches(FluidKey fluidKey);

    default FluidFilter negate() {
        return new InvertedFluidFilter(this);
    }

    default FluidFilter and(FluidFilter other) {
        return AggregateFluidFilter.and(this, other);
    }

    default FluidFilter or(FluidFilter other) {
        return AggregateFluidFilter.or(this, other);
    }

    default Predicate<FluidKey> asPredicate() {
        final FluidFilter filter = this;
        return new Predicate<FluidKey>() {
            @Override
            public boolean test(FluidKey stack) {
                if (stack == null || stack.isEmpty()) {
                    // Predicate.test doesn't have this restriction
                    return false;
                }
                return filter.matches(stack);
            }

            @Override
            public Predicate<FluidKey> negate() {
                // Because the real filter might have optimisations in negate()
                return filter.negate().asPredicate();
            }

            @Override
            public Predicate<FluidKey> and(Predicate<? super FluidKey> other) {
                if (other instanceof FluidFilter) {
                    // Because the real filter might have optimisations in and()
                    return filter.and((FluidFilter) other).asPredicate();
                } else {
                    return Predicate.super.and(other);
                }
            }

            @Override
            public Predicate<FluidKey> or(Predicate<? super FluidKey> other) {
                if (other instanceof FluidFilter) {
                    // Because the real filter might have optimisations in or()
                    return filter.or((FluidFilter) other).asPredicate();
                } else {
                    return Predicate.super.and(other);
                }
            }

            @Override
            public String toString() {
                return "{FluidFilterWrapper for " + filter + "}";
            }
        };
    }
}
