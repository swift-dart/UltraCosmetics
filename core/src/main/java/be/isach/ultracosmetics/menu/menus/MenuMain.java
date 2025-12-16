package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.menu.Menu;
import be.isach.ultracosmetics.menu.buttons.ClearCosmeticButton;
import be.isach.ultracosmetics.menu.buttons.KeysButton;
import be.isach.ultracosmetics.menu.buttons.OpenChestButton;
import be.isach.ultracosmetics.menu.buttons.OpenCosmeticMenuButton;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Main {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class MenuMain extends Menu {
    private final Component title = MessageManager.getMessage("Menu.Main.Title");

    public MenuMain(UltraCosmetics ultraCosmetics) {
        super("main", ultraCosmetics);
    }

    @Override
    public void open(UltraPlayer player) {
        super.open(player);
    }

    @Override
    protected int getSize() {
        return 9; // 1 row only
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer player) {
        // Fixed layout: Pets=0, Gadgets=1, Effects=2, Mounts=3, Hats=4, Clear=8
        for (Category category : Category.enabled()) {
            int slot = -1;
            switch (category) {
                case PETS:
                    slot = 0;
                    break;
                case GADGETS:
                    slot = 1;
                    break;
                case EFFECTS:
                    slot = 2;
                    break;
                case MOUNTS:
                    slot = 3;
                    break;
                case HATS:
                    slot = 4;
                    break;
                default:
                    continue; // Skip other categories
            }
            putItem(inventory, slot, new OpenCosmeticMenuButton(getUltraCosmetics(), category), player);
        }
        putItem(inventory, 8, new ClearCosmeticButton(), player);
    }

    protected boolean canSee(Player player, Category category) {
        return player.hasPermission(ultraCosmetics.getMenus().getCategoryMenu(category).getPermission());
    }

    protected int countVisibleCategories(Player player) {
        int total = 0;
        boolean suits = false;
        for (Category category : Category.enabled()) {
            if (category.isSuits()) {
                if (suits) continue;
                suits = true;
            }
            if (canSee(player, category)) {
                total++;
            }
        }
        return total;
    }

    protected int[] makeLayout(int visible) {
        int[] layout = null;
        switch (visible) {
            case 10:
                layout = new int[] {9, 11, 13, 15, 17, 27, 29, 31, 33, 35};
                break;
            case 9:
                layout = new int[] {9, 11, 13, 15, 17, 28, 30, 32, 34};
                break;
            case 8:
                layout = new int[] {10, 12, 14, 16, 28, 30, 32, 34};
                break;
            case 7:
                layout = new int[] {10, 13, 16, 28, 30, 32, 34};
                break;
            case 6:
                layout = new int[] {10, 13, 16, 28, 31, 34};
                break;
            case 5:
                layout = new int[] {10, 16, 22, 29, 33};
                break;
            case 4:
                layout = new int[] {19, 21, 23, 25};
                break;
            case 3:
                layout = new int[] {20, 22, 24};
                break;
            case 2:
                layout = new int[] {21, 23};
                break;
            case 1:
                layout = new int[] {22};
                break;
            case 0:
                layout = new int[] {};
                break;
        }

        if (UltraCosmeticsData.get().areTreasureChestsEnabled() && layout != null) {
            for (int i = 0; i < layout.length; i++) {
                layout[i] += 9;
            }
        }
        return layout;
    }

    @Override
    protected Component getName() {
        return title;
    }

    

}
