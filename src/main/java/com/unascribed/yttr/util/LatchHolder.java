package com.unascribed.yttr.util;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Either;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Holder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class LatchHolder<T> extends AbstractLatchReference<Holder<T>> implements Holder<T> {

	private LatchHolder() {}
	
	@Override
	public T value() {
		return get().value();
	}

	@Override
	public boolean isBound() {
		return isPresent() && get().isBound();
	}

	@Override
	public boolean isRegistryKeyId(Identifier id) {
		return isPresent() && get().isRegistryKeyId(id);
	}

	@Override
	public boolean isRegistryKey(RegistryKey<T> registryKey) {
		return isPresent() && get().isRegistryKey(registryKey);
	}

	@Override
	public boolean matches(Predicate<RegistryKey<T>> predicate) {
		return isPresent() && get().matches(predicate);
	}

	@Override
	public boolean hasTag(TagKey<T> tag) {
		return isPresent() && get().hasTag(tag);
	}

	@Override
	public Stream<TagKey<T>> streamTags() {
		return isPresent() ? Stream.empty() : get().streamTags();
	}

	@Override
	public Either<RegistryKey<T>, T> unwrap() {
		return get().unwrap();
	}

	@Override
	public Optional<RegistryKey<T>> getKey() {
		return isPresent() ? get().getKey() : Optional.empty();
	}

	@Override
	public Kind getKind() {
		return get().getKind();
	}

	@Override
	public boolean isRegistry(Registry<T> registry) {
		return isPresent() && get().isRegistry(registry);
	}
	
	/**
	 * @return an unset latch
	 */
	public static <T> LatchHolder<T> unset() {
		return new LatchHolder<>();
	}
	
	/**
	 * @return a set empty latch
	 */
	public static <T> LatchHolder<T> empty() {
		LatchHolder<T> lr = new LatchHolder<>();
		lr.setEmpty();
		return lr;
	}
	
	/**
	 * @return a set latch with the given value
	 */
	public static <T> LatchHolder<T> of(Holder<T> t) {
		LatchHolder<T> lr = new LatchHolder<>();
		lr.set(t);
		return lr;
	}

}
