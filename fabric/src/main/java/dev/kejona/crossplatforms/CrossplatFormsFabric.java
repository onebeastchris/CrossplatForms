package dev.kejona.crossplatforms;

import com.google.inject.Module;
import dev.kejona.crossplatforms.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import org.bstats.charts.CustomChart;

import java.util.List;

public class CrossplatFormsFabric implements ModInitializer, CrossplatFormsBootstrap {

    @Override
    public void onInitialize() {

    }

    @Override
    public List<Module> configModules() {
        return null;
    }

    @Override
    public void preConfigLoad(ConfigManager configManager) {

    }

    @Override
    public void addCustomChart(CustomChart chart) {

    }
}