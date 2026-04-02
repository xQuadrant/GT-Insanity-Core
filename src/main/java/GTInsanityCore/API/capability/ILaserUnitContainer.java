package GTInsanityCore.API.capability;

public interface ILaserUnitContainer {

    long getStoredLaserUnits();

    long getLaserUnitCapacity();

    long addLaserUnits(long amount, boolean simulate);

    long removeLaserUnits(long amount, boolean simulate);

    long getMaxLaserTransfer();

    int getLaserTier();
}
