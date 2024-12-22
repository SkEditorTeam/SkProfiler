package me.glicz.skprofiler;

import ch.njol.skript.Skript;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SkProfiler {
	private static SkProfiler instance;

	public final File script, output;
	private final Int2ObjectMap<LongList> records = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());

	private SkProfiler(File script, File output) {
		this.script = script;
		this.output = output;
	}

	public static void init(File script, File output) {
		Preconditions.checkState(instance == null);

		instance = new SkProfiler(script, output);
	}

	public static SkProfiler get() {
		return instance;
	}

	public void record(int line, long time) {
		records.computeIfAbsent(line, $ -> new LongArrayList()).add(time);
	}

	public void saveResults() {
		try (FileWriter writer = new FileWriter(output, StandardCharsets.UTF_8)) {
			new Gson().toJson(records, writer);
		} catch (IOException e) {
			Skript.getInstance().getSLF4JLogger().error("Failed to save profiler results", e);
		}
	}
}
