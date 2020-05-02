package com.mynote.file;

import com.mynote.main.NoteApp;
import com.mynote.common.Wrappable;
import com.mynote.content.ContentPane;
import com.mynote.content.TabComponent;
import com.mynote.content.TabContainer;
import com.mynote.resizable.ResizableWrapper;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class SaveFileManager {
    static class SaveObject implements Serializable {
        String tabTitle;
        Dimension contentPreferredSize;
        Insets contentInsets;
        ArrayList<Rectangle> wrapperBoundList = new ArrayList<>();
        ArrayList<ComponentInfo> componentInfoList = new ArrayList<>();
    }

    public static TabContainer getSavedComponents(String path) {
        ArrayList<SaveObject> saveObjects = readObjectFromDisk(path);
        TabContainer container = new TabContainer();

        for (SaveObject saveObject : saveObjects) {
            TabComponent tabComponent = new TabComponent(container, saveObject.tabTitle);
            ContentPane contentPane = new ContentPane();
            contentPane.innerPane.setPreferredSize(saveObject.contentPreferredSize);
            contentPane.insets = saveObject.contentInsets;

            Iterator<Rectangle> wrapperBoundIterator = saveObject.wrapperBoundList.iterator();
            Iterator<ComponentInfo> componentInfoIterator = saveObject.componentInfoList.iterator();

            while (wrapperBoundIterator.hasNext() &&
                    componentInfoIterator.hasNext()) {

                ComponentInfo componentInfo = componentInfoIterator.next();
                Rectangle bounds = wrapperBoundIterator.next();

                Wrappable wrappable = (Wrappable) componentInfo.restoreFromObjectInfo();
                contentPane.addComponent( wrappable.createWrapper(bounds.getSize()), bounds.getLocation());
            }

            container.createNewPage(tabComponent, contentPane);
        }

        return container;
    }


    @SuppressWarnings("unchecked")
    public static ArrayList<SaveObject> readObjectFromDisk(String path) {
        ArrayList<SaveObject> saveObject = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            saveObject = (ArrayList<SaveObject>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return saveObject;
    }


    public static ArrayList<SaveObject> createSaveObjects() {
        ArrayList<SaveObject> saveObjects = new ArrayList<>();
        TabContainer tabContainer = NoteApp.mainWindow.getContentSection();

        for (int i = 0; i < tabContainer.getTabCount() - 1; ++i) {
            ContentPane contentPane = (ContentPane) tabContainer.getComponentAt(i);
            TabComponent tabComponent = (TabComponent) tabContainer.getTabComponentAt(i);

            SaveObject saveObject = new SaveObject();
            saveObject.tabTitle = tabComponent.getTitle().getText();
            saveObject.contentPreferredSize = contentPane.innerPane.getPreferredSize();
            saveObject.contentInsets = contentPane.insets;

            Component [] components = contentPane.innerPane.getComponents();

            for (Component wrapper: components) {
                saveObject.wrapperBoundList.add(wrapper.getBounds());
                InfoExtractable extractable = (InfoExtractable) ((ResizableWrapper) wrapper).comp;
                saveObject.componentInfoList.add(extractable.convertToInfoObject());
            }

            saveObjects.add(saveObject);
        }

        return saveObjects;
    }

    public static void writeObjectToDisk(String path) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(createSaveObjects());
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
