package GTInsanityCore.common.laser;

public interface ILaserUnitHolder {

    long getStoredLaserUnits();

    long getLaserUnitCapacity();

    long receiveLaserUnits(long amount, boolean simulate);

    long extractLaserUnits(long amount, boolean simulate);
}
