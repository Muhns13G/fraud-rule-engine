package com.capitec.fraudengine.infrastructure.config;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.capitec.fraudengine.domain.model.enums.MerchantCategory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Centralized, validated configuration for fraud-rule thresholds and windows.
 */
@Validated
@ConfigurationProperties(prefix = "app.fraud-rules")
public class FraudRuleProperties {

	@Valid
	@NotNull
	private HighAmount highAmount = new HighAmount();

	@Valid
	@NotNull
	private Velocity velocity = new Velocity();

	@Valid
	@NotNull
	private RiskyMerchantCategory riskyMerchantCategory = new RiskyMerchantCategory();

	@Valid
	@NotNull
	private UnusualTime unusualTime = new UnusualTime();

	@Valid
	@NotNull
	private LocationAnomaly locationAnomaly = new LocationAnomaly();

	public HighAmount getHighAmount() {
		return highAmount;
	}

	public void setHighAmount(HighAmount highAmount) {
		this.highAmount = highAmount;
	}

	public Velocity getVelocity() {
		return velocity;
	}

	public void setVelocity(Velocity velocity) {
		this.velocity = velocity;
	}

	public RiskyMerchantCategory getRiskyMerchantCategory() {
		return riskyMerchantCategory;
	}

	public void setRiskyMerchantCategory(RiskyMerchantCategory riskyMerchantCategory) {
		this.riskyMerchantCategory = riskyMerchantCategory;
	}

	public UnusualTime getUnusualTime() {
		return unusualTime;
	}

	public void setUnusualTime(UnusualTime unusualTime) {
		this.unusualTime = unusualTime;
	}

	public LocationAnomaly getLocationAnomaly() {
		return locationAnomaly;
	}

	public void setLocationAnomaly(LocationAnomaly locationAnomaly) {
		this.locationAnomaly = locationAnomaly;
	}

	public static class HighAmount {

		@NotNull
		@DecimalMin("0.00")
		private BigDecimal reviewThreshold = new BigDecimal("10000.00");

		@NotNull
		@DecimalMin("0.00")
		private BigDecimal blockThreshold = new BigDecimal("25000.00");

		public BigDecimal getReviewThreshold() {
			return reviewThreshold;
		}

		public void setReviewThreshold(BigDecimal reviewThreshold) {
			this.reviewThreshold = reviewThreshold;
		}

		public BigDecimal getBlockThreshold() {
			return blockThreshold;
		}

		public void setBlockThreshold(BigDecimal blockThreshold) {
			this.blockThreshold = blockThreshold;
		}
	}

	public static class Velocity {

		@Min(1)
		private int thresholdCount = 3;

		@Min(1)
		private int windowMinutes = 5;

		public int getThresholdCount() {
			return thresholdCount;
		}

		public void setThresholdCount(int thresholdCount) {
			this.thresholdCount = thresholdCount;
		}

		public int getWindowMinutes() {
			return windowMinutes;
		}

		public void setWindowMinutes(int windowMinutes) {
			this.windowMinutes = windowMinutes;
		}
	}

	public static class RiskyMerchantCategory {

		@NotEmpty
		private List<MerchantCategory> flaggedCategories = List.of(
			MerchantCategory.GAMBLING,
			MerchantCategory.CRYPTO,
			MerchantCategory.MONEY_TRANSFER
		);

		public List<MerchantCategory> getFlaggedCategories() {
			return flaggedCategories;
		}

		public void setFlaggedCategories(List<MerchantCategory> flaggedCategories) {
			this.flaggedCategories = flaggedCategories;
		}
	}

	public static class UnusualTime {

		@NotNull
		private LocalTime start = LocalTime.MIDNIGHT;

		@NotNull
		private LocalTime end = LocalTime.of(4, 0);

		public LocalTime getStart() {
			return start;
		}

		public void setStart(LocalTime start) {
			this.start = start;
		}

		public LocalTime getEnd() {
			return end;
		}

		public void setEnd(LocalTime end) {
			this.end = end;
		}
	}

	public static class LocationAnomaly {

		@Min(1)
		private int scoreContribution = 40;

		private boolean compareCityWhenCountryMatches = true;

		public int getScoreContribution() {
			return scoreContribution;
		}

		public void setScoreContribution(int scoreContribution) {
			this.scoreContribution = scoreContribution;
		}

		public boolean isCompareCityWhenCountryMatches() {
			return compareCityWhenCountryMatches;
		}

		public void setCompareCityWhenCountryMatches(boolean compareCityWhenCountryMatches) {
			this.compareCityWhenCountryMatches = compareCityWhenCountryMatches;
		}
	}
}
