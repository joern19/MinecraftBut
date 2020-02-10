package gui;

import games.EverythingIsOneHit;
import games.LimitedSlots;
import games.MiniGames;
import games.MobsAreFaster;
import games.PlayerAreSwitching;
import games.RandomEffect;
import games.SlowChunkDeletion;
import games.Veggi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import minecraftbut.CommandManager;
import minecraftbut.MyTimer;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import utilities.BasicFunctions;
import utilities.Game;

public class AdminGUI {

    public static final String NAME_MAIN = "Settings";
    public static final String NAME_GAMERULES = "Edit Gamerules";
    public static final String NAME_GAMES = "Games";

    public static final String NAME_GAME_VEGGI = "Veggi";
    public static final String NAME_GAME_LIMITED_SLOTS = "Begrenzte Slots";
    public static final String NAME_GAME_PLAYER_SWITCHING = "Spieler tauschen";
    public static final String NAME_GAME_RANDOM_EFFECT = "Zuffälliger Effect";
    public static final String NAME_GAME_MOBS_FASTER = "Mobs sind Schneller";
    public static final String NAME_GAME_EVERYTHING_ONE_HIT = "Alles ist one hit.";
    public static final String NAME_GAME_SLOW_CHUNK_DELETION = "Chunks werden Gelöscht.";
    public static final String NAME_GAME_MINIGAME = "Mini games";

    public static final String NAME_GAME_RANDOM_EFFECT_SELECT_EFFECTS = "Eraubte Effect des Random Effect Modules";

    private static AdminGUI instance = null;

    private AdminGUI() {
    }

    private static boolean isGameEnabled(Class<?> c) {
        return minecraftbut.MinecraftBut.getInstance().activeGames.stream().anyMatch((active) -> (c == active.getClass()));
    }

    private static void enableGame(Game g) {
        minecraftbut.MinecraftBut.getInstance().activeGames.add(g);
    }

    private static void disableGame(Class<?> c) {
        Game toDelete = null;
        for (Game g : minecraftbut.MinecraftBut.getInstance().activeGames) {
            if (g.getClass() == c) {
                toDelete = g;
            }
        }
        minecraftbut.MinecraftBut.getInstance().activeGames.remove(toDelete);
    }

    public static synchronized void reload() { // here we should create the Pages
        if (instance == null) {
            instance = new AdminGUI();

            //do stuff that not have to be reloaded
            new Page(NAME_MAIN, instance.getMainItems());
            new Page(NAME_GAMERULES, instance.getGameruleItems());
            new Page(NAME_GAMES, instance.getSettingsItems());

            new Page(NAME_GAME_VEGGI, instance.getVeggiItems());
            new Page(NAME_GAME_LIMITED_SLOTS, instance.getLimitedSlotsItems());
            new Page(NAME_GAME_EVERYTHING_ONE_HIT, instance.getEverythingOneHitItems());
            new Page(NAME_GAME_PLAYER_SWITCHING, instance.getPlayerSwitchingItems());
            new Page(NAME_GAME_MOBS_FASTER, instance.getMobsFasterItems());
            new Page(NAME_GAME_MINIGAME, instance.getMiniGameItems());
            new Page(NAME_GAME_RANDOM_EFFECT, instance.getRandomEffectItems());
            new Page(NAME_GAME_SLOW_CHUNK_DELETION, instance.getSlowChunkDeletionItems());
            
            new Page(NAME_GAME_RANDOM_EFFECT_SELECT_EFFECTS, instance.getAllowedEffectItems());
        }
    }

    private ClickableItem[] getMiniGameItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();
        ll.add(new ClickableItem(Material.CLOCK, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                if (isGameEnabled(MiniGames.class)) {
                    disableGame(MiniGames.class);
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    enableGame(new MiniGames());
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.ORANGE_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An, noch im Alpha Testing");
                    editable.setItemMeta(im);
                }
            }
            @Override
            void onLoad(Player p) {
                if (!isGameEnabled(MiniGames.class)) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.ORANGE_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An, noch im Alpha Testing");
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.OAK_DOOR, "Zurück") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAMES).openPage(p);
            }
        });
        return ll.toArray(new ClickableItem[ll.size()]);
    }

    private ClickableItem[] getAllowedEffectItems() {
        final LinkedList<ClickableItem> list = new LinkedList<>();
        list.add(new ClickableItem(Material.MILK_BUCKET, "Alle verbieten") {
            @Override
            public void click(Player p, Boolean shift, Object[] infos) {
                RandomEffect.allowedEffects = new PotionEffectType[]{};
                list.forEach((im) -> {
                    im.onLoad(p);
                });
            }
        });
        list.add(new ClickableItem(Material.REDSTONE, "Alle hinzufügen", "SHIFT: zufällig", "SHIFT: jeder potion hat eine 50/50 chance gewählt zu werden.") {
            @Override
            void click(Player p, Boolean shift) {
                if (shift) {
                    LinkedList<PotionEffectType> newList = new LinkedList<>();
                    for (PotionEffectType pet : PotionEffectType.values()) {
                        if (Math.random() < 0.5) {
                            newList.add(pet);
                        }
                    }
                    RandomEffect.allowedEffects = newList.toArray(new PotionEffectType[newList.size()]);
                } else {
                    RandomEffect.allowedEffects = PotionEffectType.values();
                }
                list.forEach((im) -> {
                    im.onLoad(p);
                });
            }
        });
        for (PotionEffectType pet : PotionEffectType.values()) {
            ItemStack potion = new ItemStack(Material.POTION);  //if i move this
            PotionMeta meta = (PotionMeta) potion.getItemMeta(); //and this above the loop the first effect gets added to the Player. WHY???
            meta.setDisplayName(pet.getName());
            meta.setColor(pet.getColor());
            list.add(new ClickableItem(Material.POTION, meta) {
                @Override
                void click(Player p, Boolean shift) {
                    PotionMeta im = (PotionMeta) this.getTmpEditableItem(p).getItemMeta();
                    PotionEffectType pet = PotionEffectType.getByName(im.getDisplayName());
                    ArrayList<PotionEffectType> newList = new ArrayList<>(Arrays.asList(RandomEffect.allowedEffects));
                    if (newList.contains(pet)) {
                        newList.remove(pet);
                    } else {
                        newList.add(pet);
                    }
                    RandomEffect.allowedEffects = newList.toArray(new PotionEffectType[newList.size()]);
                    this.onLoad(p);
                }

                @Override
                void onLoad(Player p) {
                    PotionMeta im = (PotionMeta) this.getTmpEditableItem(p).getItemMeta();
                    PotionEffectType pet = PotionEffectType.getByName(im.getDisplayName());
                    if (im.hasCustomEffect(pet)) {
                        im.removeCustomEffect(pet);
                    }
                    if (Arrays.asList(RandomEffect.allowedEffects).contains(pet)) {
                        im.addCustomEffect(pet.createEffect(0, 0), true);
                    }
                    this.getTmpEditableItem(p).setItemMeta(im);
                }
            });
        }
        list.add(new ClickableItem(Material.OAK_DOOR, "Zurück") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAME_RANDOM_EFFECT).openPage(p);
            }
        });
        return list.toArray(new ClickableItem[list.size()]);
    }

    private ClickableItem[] getSlowChunkDeletionItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();
        ll.add(new ClickableItem(Material.CLOCK, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                if (isGameEnabled(SlowChunkDeletion.class)) {
                    disableGame(SlowChunkDeletion.class);
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    enableGame(new SlowChunkDeletion());
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                }
            }

            @Override
            void onLoad(Player p) {
                if (!isGameEnabled(SlowChunkDeletion.class)) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.CLOCK, "Die Zeit(in 1/10 Sekunden) zwischen den Block löschungen.") {
            @Override
            void leftClick(Player p, Boolean shift) {
                double step = 0.1;
                if (shift) {
                    step = 0.5;
                }
                SlowChunkDeletion.pauseBetweenDeletions += step;
                if (SlowChunkDeletion.pauseBetweenDeletions > 6.0) {
                    SlowChunkDeletion.pauseBetweenDeletions = 6.0;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount((int) (SlowChunkDeletion.pauseBetweenDeletions * 10));
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                double step = 0.1;
                if (shift) {
                    step = 0.5;
                }
                SlowChunkDeletion.pauseBetweenDeletions -= step;
                if (SlowChunkDeletion.pauseBetweenDeletions < 0.1) {
                    SlowChunkDeletion.pauseBetweenDeletions = 0.1;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount((int) (SlowChunkDeletion.pauseBetweenDeletions * 10));
            }

            @Override
            void onLoad(Player p) {
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount((int) (SlowChunkDeletion.pauseBetweenDeletions * 10));
            }
        });
        ll.add(new ClickableItem(Material.OAK_DOOR, "Zurück") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAMES).openPage(p);
            }
        });
        return ll.toArray(new ClickableItem[ll.size()]);
    }
    
    private ClickableItem[] getRandomEffectItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();
        ll.add(new ClickableItem(Material.CLOCK, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                if (isGameEnabled(RandomEffect.class)) {
                    disableGame(RandomEffect.class);
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    enableGame(new RandomEffect());
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                }
            }

            @Override
            void onLoad(Player p) {
                if (!isGameEnabled(RandomEffect.class)) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.CHICKEN, "Wie viele Sekunden §umindestens§r vergehen müssen damit ein neuer Effect Hinzugefügt wird.", "multiplier = 10 d.h. 1 sind 10 sekunden") {
            @Override
            void leftClick(Player p, Boolean shift) {
                int step = 10;
                if (shift) {
                    step = 50;
                }
                RandomEffect.applyEverySecoundsMin += step;
                if (RandomEffect.applyEverySecoundsMin > 640) {
                    RandomEffect.applyEverySecoundsMin = 640;
                }
                if (RandomEffect.applyEverySecoundsMax < RandomEffect.applyEverySecoundsMin) {
                    RandomEffect.applyEverySecoundsMin = RandomEffect.applyEverySecoundsMax;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(RandomEffect.applyEverySecoundsMin / 10);
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                int step = 10;
                if (shift) {
                    step = 50;
                }
                RandomEffect.applyEverySecoundsMin -= step;
                if (RandomEffect.applyEverySecoundsMin < 10) {
                    RandomEffect.applyEverySecoundsMin = 10;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(RandomEffect.applyEverySecoundsMin / 10);
            }

            @Override
            void onLoad(Player p) {
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(RandomEffect.applyEverySecoundsMin / 10);
            }
        });
        ll.add(new ClickableItem(Material.COOKED_CHICKEN, "Wie viele Sekunden §umaximal§r vergehen dürfen bis ein neuer Effect hinzugefügt wird.", "multiplier = 10 d.h. 1 sind 10 sekunden") {
            @Override
            void leftClick(Player p, Boolean shift) {
                int step = 10;
                if (shift) {
                    step = 50;
                }
                RandomEffect.applyEverySecoundsMax += step;
                if (RandomEffect.applyEverySecoundsMax > 640) {
                    RandomEffect.applyEverySecoundsMax = 640;
                }
                onLoad(p);
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                int step = 10;
                if (shift) {
                    step = 50;
                }
                RandomEffect.applyEverySecoundsMax -= step;
                if (RandomEffect.applyEverySecoundsMax < 10) {
                    RandomEffect.applyEverySecoundsMax = 10;
                }
                if (RandomEffect.applyEverySecoundsMax < RandomEffect.applyEverySecoundsMin) {
                    RandomEffect.applyEverySecoundsMax = RandomEffect.applyEverySecoundsMin;
                }
                onLoad(p);
            }

            @Override
            void onLoad(Player p) {
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(RandomEffect.applyEverySecoundsMax / 10);
            }
        });
        ll.add(new ClickableItem(Material.REDSTONE, "Wie viele Sekunden der Effect andauert.", "multiplier = 10 d.h. 1 sind 10 sekunden") {
            @Override
            void leftClick(Player p, Boolean shift) {
                int step = 10;
                if (shift) {
                    step = 50;
                }
                RandomEffect.effectLength += step;
                if (RandomEffect.effectLength > 640) {
                    RandomEffect.effectLength = 640;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(RandomEffect.effectLength / 10);
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                int step = 10;
                if (shift) {
                    step = 50;
                }
                RandomEffect.effectLength -= step;
                if (RandomEffect.effectLength < 10) {
                    RandomEffect.effectLength = 10;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(RandomEffect.effectLength / 10);
            }

            @Override
            void onLoad(Player p) {
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(RandomEffect.effectLength / 10);
            }
        });
        ll.add(new ClickableItem(Material.CHICKEN, "Effect Level", "Das Level des Effects") {
            @Override
            void leftClick(Player p, Boolean shift) {
                int step = 1;
                if (shift) {
                    step = 5;
                }
                RandomEffect.effectLevel += step;
                if (RandomEffect.effectLevel > 25) {
                    RandomEffect.effectLevel = 25;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(RandomEffect.effectLevel);
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                int step = 1;
                if (shift) {
                    step = 5;
                }
                RandomEffect.effectLevel -= step;
                if (RandomEffect.effectLevel < 1) {
                    RandomEffect.effectLevel = 1;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(RandomEffect.effectLevel);
            }

            @Override
            void onLoad(Player p) {
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(RandomEffect.effectLevel);
            }
        });
        ll.add(new ClickableItem(Material.CLOCK, "Loading") {
            @Override
            void click(Player p, Boolean shift) {
                RandomEffect.everyPlayerDiffrentEffect = !RandomEffect.everyPlayerDiffrentEffect;
                onLoad(p);
            }

            @Override
            void onLoad(Player p) {
                if (RandomEffect.everyPlayerDiffrentEffect) {
                    ItemStack editable = getTmpEditableItem(p);
                    editable.setType(Material.GOLD_INGOT);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Jeder bekommt einen anderen Effect: Ja");
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = getTmpEditableItem(p);
                    editable.setType(Material.GOLD_BLOCK);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Jeder bekommt einen anderen Effect: Nein");
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.POTION, "Edit erlaubte effecte") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAME_RANDOM_EFFECT_SELECT_EFFECTS).openPage(p);
            }
        });
        ll.add(new ClickableItem(Material.OAK_DOOR, "Zurück") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAMES).openPage(p);
            }
        });
        return ll.toArray(new ClickableItem[ll.size()]);
    }

    private ClickableItem[] getMobsFasterItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();
        ll.add(new ClickableItem(Material.CLOCK, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                if (isGameEnabled(MobsAreFaster.class)) {
                    disableGame(MobsAreFaster.class);
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    enableGame(new MobsAreFaster());
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                }
            }

            @Override
            void onLoad(Player p) {
                if (!isGameEnabled(MobsAreFaster.class)) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.CHICKEN, "Speed Level") {
            @Override
            void leftClick(Player p, Boolean shift) {
                int step = 1;
                if (shift) {
                    step = 5;
                }
                MobsAreFaster.SPEED_LEVEL += step;
                if (MobsAreFaster.SPEED_LEVEL > 25) {
                    MobsAreFaster.SPEED_LEVEL = 25;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(MobsAreFaster.SPEED_LEVEL);
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                int step = 1;
                if (shift) {
                    step = 5;
                }
                MobsAreFaster.SPEED_LEVEL -= step;
                if (MobsAreFaster.SPEED_LEVEL < 1) {
                    MobsAreFaster.SPEED_LEVEL = 1;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(MobsAreFaster.SPEED_LEVEL);
            }

            @Override
            void onLoad(Player p) {
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(MobsAreFaster.SPEED_LEVEL);
            }
        });
        ll.add(new ClickableItem(Material.OAK_DOOR, "Zurück") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAMES).openPage(p);
            }
        });
        return ll.toArray(new ClickableItem[ll.size()]);
    }

    private ClickableItem[] getPlayerSwitchingItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();
        ll.add(new ClickableItem(Material.CLOCK, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                if (isGameEnabled(PlayerAreSwitching.class)) {
                    disableGame(PlayerAreSwitching.class);
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    enableGame(new PlayerAreSwitching());
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                }
            }

            @Override
            void onLoad(Player p) {
                if (!isGameEnabled(PlayerAreSwitching.class)) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.CHICKEN, "Wie viele Sekunden §umindestens§r vergehen müssen damit die Spieler Getauscht werden.", "multiplier = 10 d.h. 1 sind 10 sekunden") {
            @Override
            void leftClick(Player p, Boolean shift) {
                int step = 10;
                if (shift) {
                    step = 50;
                }
                PlayerAreSwitching.switchEveryXSecoundsMin += step;
                if (PlayerAreSwitching.switchEveryXSecoundsMin > 640) {
                    PlayerAreSwitching.switchEveryXSecoundsMin = 640;
                }
                if (PlayerAreSwitching.switchEveryXSecoundsMax < PlayerAreSwitching.switchEveryXSecoundsMin) {
                    PlayerAreSwitching.switchEveryXSecoundsMin = PlayerAreSwitching.switchEveryXSecoundsMax;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(PlayerAreSwitching.switchEveryXSecoundsMin / 10);
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                int step = 10;
                if (shift) {
                    step = 50;
                }
                PlayerAreSwitching.switchEveryXSecoundsMin -= step;
                if (PlayerAreSwitching.switchEveryXSecoundsMin < 10) {
                    PlayerAreSwitching.switchEveryXSecoundsMin = 10;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(PlayerAreSwitching.switchEveryXSecoundsMin / 10);
            }

            @Override
            void onLoad(Player p) {
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(PlayerAreSwitching.switchEveryXSecoundsMin / 10);
            }
        });
        ll.add(new ClickableItem(Material.COOKED_CHICKEN, "Wie viele Sekunden §umaximal§r vergehen dürfen bis die Spieler Getauscht werden.", "multiplier = 10 d.h. 1 sind 10 sekunden") {
            @Override
            void leftClick(Player p, Boolean shift) {
                int step = 10;
                if (shift) {
                    step = 50;
                }
                PlayerAreSwitching.switchEveryXSecoundsMax += step;
                if (PlayerAreSwitching.switchEveryXSecoundsMax > 640) {
                    PlayerAreSwitching.switchEveryXSecoundsMax = 640;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(PlayerAreSwitching.switchEveryXSecoundsMax / 10);
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                int step = 10;
                if (shift) {
                    step = 50;
                }
                PlayerAreSwitching.switchEveryXSecoundsMax -= step;
                if (PlayerAreSwitching.switchEveryXSecoundsMax < 10) {
                    PlayerAreSwitching.switchEveryXSecoundsMax = 10;
                }
                if (PlayerAreSwitching.switchEveryXSecoundsMax < PlayerAreSwitching.switchEveryXSecoundsMin) {
                    PlayerAreSwitching.switchEveryXSecoundsMax = PlayerAreSwitching.switchEveryXSecoundsMin;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(PlayerAreSwitching.switchEveryXSecoundsMax / 10);
            }

            @Override
            void onLoad(Player p) {
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(PlayerAreSwitching.switchEveryXSecoundsMax / 10);
            }
        });
        ll.add(new ClickableItem(Material.OAK_DOOR, "Zurück") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAMES).openPage(p);
            }
        });
        return ll.toArray(new ClickableItem[ll.size()]);
    }

    private ClickableItem[] getEverythingOneHitItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();
        ll.add(new ClickableItem(Material.CLOCK, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                if (isGameEnabled(EverythingIsOneHit.class)) {
                    disableGame(EverythingIsOneHit.class);
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    enableGame(new EverythingIsOneHit());
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                }
            }

            @Override
            void onLoad(Player p) {
                if (!isGameEnabled(EverythingIsOneHit.class)) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.OAK_DOOR, "Zurück") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAMES).openPage(p);
            }
        });
        return ll.toArray(new ClickableItem[ll.size()]);
    }

    private ClickableItem[] getLimitedSlotsItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();
        ll.add(new ClickableItem(Material.CLOCK, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                if (isGameEnabled(LimitedSlots.class)) {
                    disableGame(LimitedSlots.class);
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    enableGame(new LimitedSlots());
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                }
            }

            @Override
            void onLoad(Player p) {
                if (isGameEnabled(LimitedSlots.class)) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.MINECART, "Freie Slots in der Hotbar", "64 Bedeuted 0") {
            @Override
            void leftClick(Player p, Boolean shift) {
                int step = 1;
                if (shift) {
                    step = 3;
                }
                LimitedSlots.slotsInHotbar += step;
                if (LimitedSlots.slotsInHotbar > 9) {
                    LimitedSlots.slotsInHotbar = 9;
                }
                if (LimitedSlots.slotsInHotbar == 0) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setAmount(64);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setAmount(LimitedSlots.slotsInHotbar);
                }
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                if (LimitedSlots.slotsInHotbar == 0) {
                    return;
                }
                int step = 1;
                if (shift) {
                    step = 3;
                }

                LimitedSlots.slotsInHotbar -= step;
                if (LimitedSlots.slotsInHotbar < 1) {
                    LimitedSlots.slotsInHotbar = 0;
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setAmount(64);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setAmount(LimitedSlots.slotsInHotbar);
                }
            }

            @Override
            void onLoad(Player p) {
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                if (LimitedSlots.slotsInHotbar == 0) {
                    editable.setAmount(64);
                } else {
                    editable.setAmount(LimitedSlots.slotsInHotbar);
                }
            }
        });
        ll.add(new ClickableItem(Material.CHEST, "Freie Slots im Inventar", "64 Bedeuted 0") {
            @Override
            void leftClick(Player p, Boolean shift) {
                int step = 1;
                if (shift) {
                    step = 5;
                }
                LimitedSlots.slotsInInventory += step;
                if (LimitedSlots.slotsInInventory > 27) {
                    LimitedSlots.slotsInInventory = 27;
                }
                if (LimitedSlots.slotsInInventory == 0) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setAmount(64);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setAmount(LimitedSlots.slotsInInventory);
                }
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                if (LimitedSlots.slotsInInventory == 0) {
                    return;
                }
                int step = 1;
                if (shift) {
                    step = 5;
                }
                LimitedSlots.slotsInInventory -= step;
                if (LimitedSlots.slotsInInventory < 1) {
                    LimitedSlots.slotsInInventory = 0;
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setAmount(64);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setAmount(LimitedSlots.slotsInInventory);
                }
            }

            @Override
            void onLoad(Player p) {
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                if (LimitedSlots.slotsInInventory == 0) {
                    editable.setAmount(64);
                } else {
                    editable.setAmount(LimitedSlots.slotsInInventory);
                }
            }
        });
        ll.add(new ClickableItem(Material.DIAMOND_PICKAXE, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                LimitedSlots.offHandAllowed = !LimitedSlots.offHandAllowed;
                this.onLoad(p);
            }

            @Override
            void onLoad(Player p) {
                if (LimitedSlots.offHandAllowed) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("OFF Hand erlaubt: Ja");
                    im.addEnchant(Enchantment.DURABILITY, 1, false);
                    im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("OFF Hand erlaubt: Nein");
                    im.removeEnchant(Enchantment.DURABILITY);
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.IRON_HELMET, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                LimitedSlots.helmetAllowed = !LimitedSlots.helmetAllowed;
                this.onLoad(p);
            }

            @Override
            void onLoad(Player p) {
                if (LimitedSlots.helmetAllowed) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Helm erlaubt: Ja");
                    im.addEnchant(Enchantment.DURABILITY, 1, false);
                    im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Helm erlaubt: Nein");
                    im.removeEnchant(Enchantment.DURABILITY);
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.IRON_CHESTPLATE, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                LimitedSlots.chestplateAllowed = !LimitedSlots.chestplateAllowed;
                this.onLoad(p);
            }

            @Override
            void onLoad(Player p) {
                if (LimitedSlots.chestplateAllowed) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Brustplatte erlaubt: Ja");
                    im.addEnchant(Enchantment.DURABILITY, 1, false);
                    im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Brustplatte erlaubt: Nein");
                    im.removeEnchant(Enchantment.DURABILITY);
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.IRON_LEGGINGS, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                LimitedSlots.leggingsAllowed = !LimitedSlots.leggingsAllowed;
                this.onLoad(p);
            }

            @Override
            void onLoad(Player p) {
                if (LimitedSlots.leggingsAllowed) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Hose erlaubt: Ja");
                    im.addEnchant(Enchantment.DURABILITY, 1, false);
                    im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Hose erlaubt: Nein");
                    im.removeEnchant(Enchantment.DURABILITY);
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.IRON_BOOTS, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                LimitedSlots.bootsAllowed = !LimitedSlots.bootsAllowed;
                this.onLoad(p);
            }

            @Override
            void onLoad(Player p) {
                if (LimitedSlots.bootsAllowed) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Schuhe erlaubt: Ja");
                    im.addEnchant(Enchantment.DURABILITY, 1, false);
                    im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Schuhe erlaubt: Nein");
                    im.removeEnchant(Enchantment.DURABILITY);
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.OAK_DOOR, "Zurück") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAMES).openPage(p);
            }
        });
        return ll.toArray(new ClickableItem[ll.size()]);
    }

    private ClickableItem[] getVeggiItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();
        ll.add(new ClickableItem(Material.CLOCK, "Loading...") {
            @Override
            void click(Player p, Boolean shift) {
                if (isGameEnabled(Veggi.class)) {
                    disableGame(Veggi.class);
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                } else {
                    enableGame(new Veggi());
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                }
            }

            @Override
            void onLoad(Player p) {
                if (isGameEnabled(Veggi.class)) {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.GREEN_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("An");
                    editable.setItemMeta(im);
                } else {
                    ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                    editable.setType(Material.RED_WOOL);
                    ItemMeta im = editable.getItemMeta();
                    im.setDisplayName("Aus");
                    editable.setItemMeta(im);
                }
            }
        });
        ll.add(new ClickableItem(Material.CLOCK, "Wie Lange man den Potion Effect bekommt in Sekunden") {
            @Override
            void leftClick(Player p, Boolean shift) {
                int step = 1;
                if (shift) {
                    step = 10;
                }
                Veggi.secoundsPotiond += step;
                if (Veggi.secoundsPotiond > 64) {
                    Veggi.secoundsPotiond = 64;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(Veggi.secoundsPotiond);
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                int step = 1;
                if (shift) {
                    step = 10;
                }
                Veggi.secoundsPotiond -= step;
                if (Veggi.secoundsPotiond < 1) {
                    Veggi.secoundsPotiond = 1;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(Veggi.secoundsPotiond);
            }

            @Override
            void onLoad(Player p) {
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(Veggi.secoundsPotiond);
            }
        });
        ll.add(new ClickableItem(Material.WOODEN_SWORD, "Instant Damage in halben Herzen") {
            @Override
            void leftClick(Player p, Boolean shift) {
                int step = 1;
                if (shift) {
                    step = 10;
                }
                Veggi.instandDamage += step;
                if (Veggi.instandDamage > 25) {
                    Veggi.instandDamage = 25;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(Veggi.instandDamage);
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                int step = 1;
                if (shift) {
                    step = 10;
                }
                Veggi.instandDamage -= step;
                if (Veggi.instandDamage < 1) {
                    Veggi.instandDamage = 1;
                }
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(Veggi.instandDamage);
            }

            @Override
            void onLoad(Player p) {
                ItemStack editable = p.getOpenInventory().getItem(this.getSlot());
                editable.setAmount(Veggi.instandDamage);
            }
        });
        ll.add(new ClickableItem(Material.OAK_DOOR, "Zurück") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAMES).openPage(p);
            }
        });
        return ll.toArray(new ClickableItem[ll.size()]);
    }

    private ClickableItem[] getMainItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();
        ll.add(new ClickableItem(Material.STONE, NAME_GAMERULES) {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAMERULES).openPage(p);
            }
        });
        ll.add(new ClickableItem(Material.COMPARATOR, "Edit Games") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAMES).openPage(p);
            }
        });
        ll.add(new ClickableItem(Material.GREEN_WOOL, "Start") {
            @Override
            void click(Player p, Boolean shift) {
                CommandManager.getInstance().commandStart();
                MyTimer.startTimer();
            }
        });
        ll.add(new ClickableItem(Material.YELLOW_WOOL, "Pause") {
            @Override
            void click(Player p, Boolean shift) {
                CommandManager.getInstance().commandStop();
            }
        });
        ll.add(new ClickableItem(Material.RED_WOOL, "Reset") {
            @Override
            void click(Player p, Boolean shift) {
                CommandManager.getInstance().commandReset();
            }
        });
        ll.add(new ClickableItem(Material.OAK_DOOR, "EXIT") {
            @Override
            void click(Player p, Boolean shift) {
                p.closeInventory();
            }
        });
        return ll.toArray(new ClickableItem[ll.size()]);
    }

    private ClickableItem[] getGameruleItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();
        for (GameRule gr : GameRule.values()) {
            if (!gr.getType().equals(Boolean.class)) {
                continue;
            }
            ll.add(new ClickableItem(Material.STONE, "Edit: " + gr.getName(), "loading...") {
                @Override
                public void click(Player p, Boolean shift) {
                    World w = p.getWorld();
                    w.setGameRule(gr, !(Boolean) w.getGameRuleValue(gr));
                    BasicFunctions.setLore(p.getOpenInventory().getItem(this.getSlot()), "It is set to: " + w.getGameRuleValue(gr).toString());
                }

                @Override
                void onLoad(Player p) {
                    BasicFunctions.setLore(p.getOpenInventory().getItem(this.getSlot()), "It is set to: " + p.getWorld().getGameRuleValue(gr).toString());
                }
            });
        }
        return ll.toArray(new ClickableItem[ll.size()]);
    }

    private ClickableItem[] getSettingsItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();

        ll.add(new ClickableItem(Material.APPLE, "Veggi", "Esse kein Fleisch!") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAME_VEGGI).openPage(p);
            }
        });
        ll.add(new ClickableItem(Material.BARRIER, "Weniger Slots", "Deine Slots sind Begrenzt!") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAME_LIMITED_SLOTS).openPage(p);
            }
        });
        ll.add(new ClickableItem(Material.CHORUS_FRUIT, "Spieler Tauschen", "Alle Tauschen ihre Position!") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAME_PLAYER_SWITCHING).openPage(p);
            }
        });
        ll.add(new ClickableItem(Material.POTION, "Random Effect", "Manchmal bekommst du einen Effect!") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAME_RANDOM_EFFECT).openPage(p);
            }
        });
        ll.add(new ClickableItem(Material.FEATHER, "Alle Mobs sind schneller", "Sei schnell!") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAME_MOBS_FASTER).openPage(p);
            }
        });
        ll.add(new ClickableItem(Material.DIAMOND_PICKAXE, NAME_GAME_EVERYTHING_ONE_HIT) {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAME_EVERYTHING_ONE_HIT).openPage(p);
            }
        });
        ll.add(new ClickableItem(Material.TNT, "Chunk Löscher", "Alle betretene werden lansam Gelöscht.") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAME_SLOW_CHUNK_DELETION).openPage(p);
            }
        });
        ll.add(new ClickableItem(Material.MINECART, NAME_GAME_MINIGAME) {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAME_MINIGAME).openPage(p);
            }
        });
        ll.add(new ClickableItem(Material.OAK_DOOR, "Zurück") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_MAIN).openPage(p);
            }
        });
        return ll.toArray(new ClickableItem[ll.size()]);
    }
}
