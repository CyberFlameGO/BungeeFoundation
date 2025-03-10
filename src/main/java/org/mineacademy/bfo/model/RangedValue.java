package org.mineacademy.bfo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mineacademy.bfo.Common;
import org.mineacademy.bfo.RandomUtil;
import org.mineacademy.bfo.Valid;

import lombok.Getter;

/**
 * A class holding a minimum and a maximum
 */
@Getter
public final class RangedValue {

	/**
	 * The minimum
	 */
	private final Number min;

	/**
	 * The maximum
	 */
	private final Number max;

	/**
	 * Create a new static value.
	 *
	 * @param value the value
	 */
	public RangedValue(Number value) {
		this(value, value);
	}

	/**
	 * Create a new ranged value.
	 *
	 * @param min the minimum value
	 * @param max the maximum value
	 */
	public RangedValue(Number min, Number max) {
		Valid.checkBoolean(min.longValue() <= max.longValue(), "Minimum must be lower or equal maximum");

		this.min = min;
		this.max = max;
	}

	/**
	 * Get the minimum as double
	 * @return
	 */
	public double getMinDouble() {
		return min.doubleValue();
	}

	/**
	 * Get the maximum as double
	 * @return
	 */
	public double getMaxDouble() {
		return max.doubleValue();
	}

	/**
	 * Get the minimum as an long
	 * @return
	 */
	public long getMinLong() {
		return min.longValue();
	}

	/**
	 * Get the maximum as an long
	 * @return
	 */
	public long getMaxLong() {
		return max.longValue();
	}

	/**
	 * Get if the number is within the bounds
	 *
	 * @param value the number to compare
	 * @return
	 */
	public boolean isInRangeLong(long value) {
		return value >= min.longValue() && value <= max.longValue();
	}

	/**
	 * Get if the number is within the bounds
	 *
	 * @param value the number to compare
	 * @return
	 */
	public boolean isInRangeDouble(double value) {
		return value >= min.doubleValue() && value <= max.doubleValue();
	}

	/**
	 * Get a value in range between the two values we store in this class
	 *
	 * @return a random value
	 */
	public int getRandomInt() {
		return RandomUtil.nextBetween((int) getMinLong(), (int) getMaxLong());
	}

	/**
	 * Return whether the two values we store in this class are equal
	 *
	 * @return
	 */
	public boolean isStatic() {
		return min.longValue() == max.longValue();
	}

	/**
	 * Return a saveable representation (assuming saving in ticks) of this value
	 *
	 * @return
	 */
	public String toLine() {
		return min.longValue() + " - " + max.longValue();
	}

	/**
	 * Create a {@link RangedValue} from a line
	 * Example: 1-10
	 * 5 - 60
	 * 4
	 * <p>
	 * or
	 * <p>
	 * 10 seconds - 20 minutes (will be converted to seconds)
	 * @param line
	 * @return
	 */
	public static RangedValue parse(String line) {

		String parts[];

		// Support negative values
		if (line.split("\\-").length != 2) {
			final Pattern pattern = Pattern.compile("(-|)[0-9]{1,}");
			final Matcher matcher = pattern.matcher(line);
			final List<String> found = new ArrayList<>();

			while (matcher.find())
				found.add(matcher.group());

			parts = Common.toArray(found);
		}

		else
			parts = line.startsWith("-") ? new String[] { line } : line.split("\\-");

		Valid.checkBoolean(parts.length == 1 || parts.length == 2, "Malformed value " + line);

		final String first = parts[0].trim();
		final String second = parts.length == 2 ? parts[1].trim() : first;

		// Check if valid numbers
		Valid.checkBoolean(Valid.isNumber(first),
				"Invalid ranged value 1. input: '" + first + "' from line: '" + line + "'. RangedValue no longer accepts human natural format, for this, use RangedSimpleTime instead.");

		Valid.checkBoolean(Valid.isNumber(second),
				"Invalid ranged value 2. input: '" + second + "' from line: '" + line + "'. RangedValue no longer accepts human natural format, for this, use RangedSimpleTime instead.");

		final Number firstNumber = first.contains(".") ? Double.parseDouble(first) : Long.parseLong(first);
		final Number secondNumber = second.contains(".") ? Double.parseDouble(second) : Long.parseLong(second);

		// Check if 1<2
		if (first.contains("."))
			Valid.checkBoolean(firstNumber.longValue() <= secondNumber.longValue(),
					"First number cannot be greater than second: " + firstNumber.longValue() + " vs " + secondNumber.longValue() + " in " + line);

		else
			Valid.checkBoolean(firstNumber.doubleValue() <= secondNumber.doubleValue(),
					"First number cannot be greater than second: " + firstNumber.doubleValue() + " vs " + secondNumber.doubleValue() + " in " + line);

		return new RangedValue(firstNumber, secondNumber);
	}

	@Override
	public String toString() {
		return isStatic() ? min.longValue() + "" : min.longValue() + " - " + max.longValue();
	}
}
