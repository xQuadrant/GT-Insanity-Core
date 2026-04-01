package GTInsanityCore.API.managers;

import GTInsanityCore.API.interaction.IFallingBlockInteraction;

public interface IFallingBlockInteractionManager {

    void registerInteraction(IFallingBlockInteraction interaction);

    void unregisterInteraction(IFallingBlockInteraction interaction);

    int getInteractionCount();
}
