package ctu.game.isometric.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ctu.game.isometric.IsometricGame;
import ctu.game.isometric.controller.GameController;
import ctu.game.isometric.model.dictionary.Dictionary;
import ctu.game.isometric.model.game.GameState;
import ctu.game.isometric.view.renderer.CharacterRenderer;
import ctu.game.isometric.view.ui.DialogUI;
import ctu.game.isometric.view.renderer.MapRenderer;
import ctu.game.isometric.view.ui.ExploringUI;
import ctu.game.isometric.view.ui.InventoryUI;
import ctu.game.isometric.view.view.DictionaryView;

public class GameScreen implements Screen {
    private final IsometricGame game;
    private GameController gameController;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private DialogUI dialogUI; // Add DialogUI
    private ExploringUI exploringUI;
    // Renderers
    private MapRenderer mapRenderer;
    private CharacterRenderer characterRenderer;
    private boolean isCharacterCreated = false;
    private GameState currentState = GameState.MAIN_MENU;

    public GameScreen(IsometricGame game, GameController gameController) {
        this.game = game;
        this.gameController = gameController;
        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        gameController.setCamera(camera);
//        camera.setToOrtho(false, 800, 480);
//        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        batch = new SpriteBatch();
        // In GameScreen.java - when initializing MapRenderer
        dialogUI = new DialogUI(gameController.getDialogController());
        gameController.getInputController().setDialogUI(dialogUI);
        // Set input processor
        Gdx.input.setInputProcessor(gameController.getInputController());

    }

    @Override
    public void render(float delta) {
        // Cập nhật game
        gameController.update(delta);
        gameController.getTransitionController().update(delta);
        // Chỉ khởi tạo 1 lần khi gameController vừa tạo xong

        if (gameController.isCreated()) {
            cleanupForMainMenu();
            mapRenderer = new MapRenderer(
                    gameController.getMap(),
                    game.getAssetManager(),
                    gameController.getEventManager(),
                    gameController.getCharacter(),
                    camera
            );
            gameController.getInputController().setMapRenderer(mapRenderer);

            characterRenderer = new CharacterRenderer(
                    gameController.getCharacter(),
                    game.getAssetManager(),
                    mapRenderer
            );



            InventoryUI inventoryUI = new InventoryUI(gameController);
            gameController.setInventoryUI(inventoryUI);

            exploringUI = new ExploringUI(gameController);

//            exploringUI.setCharacter(gameController.getCharacter());
            // Reset dialog UI

//            if (dialogUI != null && gameController.getDialogController().isDialogActive()) {
//                dialogUI.render();
//            }


            dialogUI = new DialogUI(gameController.getDialogController());
            gameController.getInputController().setDialogUI(dialogUI);



            if(gameController.getDictionaryView() != null) {
                gameController.getDictionaryView().dispose();
            }

            gameController.resetLearnedWords();
            gameController.setDictionaryView(new DictionaryView(gameController,gameController.getDictionary(), gameController.getWordNetValidator()));


            // Reset flag
            gameController.getAchievementUI().hide();
            gameController.setCreated(false);
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        currentState = gameController.getCurrentState();
        if (gameController.getTransitionController().isTransitioning()) {
            gameController.getTransitionController().render(batch);
        } else {
            switch (currentState) {
                case MAIN_MENU:
                    gameController.getMainMenuController().render(batch);
                    break;
                case CHARACTER_CREATION:
                    gameController.getCharacterCreationController().render(batch);
                    break;
                case EXPLORING:
                    gameController.setCharacterCreationController(null);
                    gameController.setLoadGameController(null);
                    mapRenderer.render(batch);

                    if (gameController.hasActiveEvent()) {
                        mapRenderer.renderActionButton(
                                batch,
                                gameController.getCurrentEventType(),
                                gameController.getCurrentEvent(),
                                gameController.getCurrentEventX(),
                                gameController.getCurrentEventY()
                        );
                    }

                    if (characterRenderer != null) characterRenderer.render(batch);

                    // End the batch before rendering UI
                    batch.end();

                    // Render the UI on top


                    if (exploringUI != null) exploringUI.render();

                    if (gameController.getInventoryUI() != null) {
                        gameController.getInventoryUI().render(batch);
                    }
                    if(gameController.getAchievementUI().isActive()){
                        gameController.getAchievementUI().render(batch);
                    }

                    if (dialogUI != null && gameController.getDialogController().isDialogActive()) {

                        dialogUI.render();
                        batch.begin();
                        gameController.getEffectManager().render(batch);
                        batch.end();
                    }


                    // Begin the batch again for any subsequent rendering
                    batch.begin();
                    break;
                case CUTSCENE:
                    gameController.getCutsceneController().render(batch);
                    break;
                // In GameScreen.java, inside the switch statement in render()

                case DICTIONARY:
                    gameController.getDictionaryView().render(batch);
                    break;
                case GAMEPLAY:
                    gameController.getGameplayController().render(batch);
                    break;
                case QUIZZES:
                    gameController.getQuizController().render(batch);
                    break;
                case MENU:
                    gameController.getMenuController().render(batch);
                    break;
                case LOAD_GAME:
                    gameController.getLoadGameController().render(batch);
                    break;
                case SETTINGS:
                    gameController.getSettingsMenuController().render(batch);
                    break;
                default:
                    break;
            }
        }
        batch.end();
    }

    public void cleanupForMainMenu() {
        if (mapRenderer != null) {
            mapRenderer.dispose();
            mapRenderer = null;
        }
        if (exploringUI != null) {
            exploringUI.dispose();
            exploringUI = null;
        }
        if (dialogUI != null) {
            dialogUI.dispose();
            dialogUI = null;
        }
        // Consider clearing any cached data in gameController
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        camera.update();
    }

    @Override
    public void pause() {
        if (gameController.getCurrentState() == GameState.MAIN_MENU || gameController.getCurrentState() == GameState.SETTINGS) {
                return;
        }
        System.out.println("GameScreen paused");
        gameController.setState(GameState.MENU);
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        gameController.dispose();
        if (dialogUI != null) {
            dialogUI.dispose(); // Dispose DialogUI
        }
        if (exploringUI!=null)
            exploringUI.dispose();
    }
}