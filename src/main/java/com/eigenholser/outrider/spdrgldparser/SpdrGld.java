package com.eigenholser.outrider.spdrgldparser;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("id")
public class SpdrGld {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate date;
	private BigDecimal gldClosePrice;
	private BigDecimal lbmaGoldPrice;
	private BigDecimal navPerGldInGold;
	private BigDecimal navPerShare;
	private BigDecimal indicativePriceGld;
	private BigDecimal midpointSpread;
	private BigDecimal gldPremium;
	private Long dailyShareVolume;
	private BigDecimal navOunces;
	private BigDecimal navTonnes;
	private BigDecimal navUsd;

	public SpdrGld() {}
	
	public SpdrGld(LocalDate date, BigDecimal gldClosePrice, BigDecimal lbmaGoldPrice,
			BigDecimal navPerGldInGold, BigDecimal navPerShare, BigDecimal indicativePriceGld,
			BigDecimal midpointSpread, BigDecimal gldPremium, Long dailyShareVolume, BigDecimal navOunces,
			BigDecimal navTonnes, BigDecimal navUsd) {
		this.date = date;
		this.gldClosePrice = gldClosePrice;
		this.lbmaGoldPrice = lbmaGoldPrice;
		this.navPerGldInGold = navPerGldInGold;
		this.navPerShare = navPerShare;
		this.indicativePriceGld = indicativePriceGld;
		this.midpointSpread = midpointSpread;
		this.gldPremium = gldPremium;
		this.dailyShareVolume = dailyShareVolume;
		this.navOunces = navOunces;
		this.navTonnes = navTonnes;
		this.navUsd = navUsd;
	}

	public LocalDate getDate() {
		return date;
	}

	public BigDecimal getGldClosePrice() {
		return gldClosePrice;
	}

	public BigDecimal getLbmaGoldPrice() {
		return lbmaGoldPrice;
	}

	public BigDecimal getNavPerGldInGold() {
		return navPerGldInGold;
	}

	public BigDecimal getNavPerShare() {
		return navPerShare;
	}

	public BigDecimal getIndicativePriceGld() {
		return indicativePriceGld;
	}

	public BigDecimal getMidpointSpread() {
		return midpointSpread;
	}

	public BigDecimal getGldPremium() {
		return gldPremium;
	}

	public long getDailyShareVolume() {
		return dailyShareVolume;
	}

	public BigDecimal getNavOunces() {
		return navOunces;
	}

	public BigDecimal getNavTonnes() {
		return navTonnes;
	}

	public BigDecimal getNavUsd() {
		return navUsd;
	}

	@Override
	public String toString() {
		return "SpdrGld [date=" + date + ", gldClosePrice=" + gldClosePrice + ", lbmaGoldPrice="
				+ lbmaGoldPrice + ", navPerGldInGold=" + navPerGldInGold + ", navPerShare=" + navPerShare
				+ ", indicativePriceGld=" + indicativePriceGld + ", midpointSpread=" + midpointSpread + ", gldPremium="
				+ gldPremium + ", dailyShareVolume=" + dailyShareVolume + ", navOunces=" + navOunces + ", navTonnes="
				+ navTonnes + ", navUsd=" + navUsd + "]";
	}

}
