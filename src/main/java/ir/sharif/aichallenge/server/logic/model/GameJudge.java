package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.logic.model.Colony.Colony;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GameJudge {
    private final AntRepository antRepository;

    public GameJudge(AntRepository antRepository) {
        this.antRepository = antRepository;
    }

    public Colony getWinner() {
        List<Colony> aliveColonies = getAliveColonies(antRepository.getColonies());
        switch (aliveColonies.size()) {
            case 0:
                return getWinnerByAssets(antRepository.getColonies());
            case 1:
                return aliveColonies.get(0);
            default:
                return getWinnerByAssets(aliveColonies);
        }
    }

    private Colony getWinnerByAssets(List<Colony> colonies) {
        colonies.sort(new ColonyComparator().reversed());
        return colonies.get(0);
    }

    private List<Colony> getAliveColonies(List<Colony> colonies) {
        return colonies.stream().filter(x -> x.getBaseHealth() > 0).collect(Collectors.toList());
    }
}

class ColonyComparator implements Comparator<Colony> {

    @Override
    public int compare(Colony o1, Colony o2) {
        if (o1.getBaseHealth() != o2.getBaseHealth())
            return o1.getBaseHealth() - o2.getBaseHealth();
        if (o1.getAllAntsGeneratedCount() != o2.getAllAntsGeneratedCount())
            return o1.getAllAntsGeneratedCount() - o2.getAllAntsGeneratedCount();
        if (o1.getAllSoldierAntsGeneratedCount() != o2.getAllSoldierAntsGeneratedCount())
            return o1.getAllSoldierAntsGeneratedCount() - o2.getAllSoldierAntsGeneratedCount();
        if (o1.getAllResourcesAmount() != o2.getAllResourcesAmount())
            return o1.getAllResourcesAmount() - o2.getAllResourcesAmount();
        return 0;
    }
}
