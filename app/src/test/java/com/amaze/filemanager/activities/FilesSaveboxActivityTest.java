package com.amaze.filemanager.activities;

import android.content.Context;
import android.os.Environment;
import android.test.mock.MockContext;

import com.amaze.filemanager.BuildConfig;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowEnvironment;
import org.robolectric.shadows.multidex.ShadowMultiDex;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowMultiDex.class})
public class FilesSaveboxActivityTest {
    private FilesSaveboxActivity activity;
    String testPassword = "19980330";
    String testString = "ztw33";
    String filename = "text.txt";

    @Before
    public void setUp() {
        activity = new FilesSaveboxActivity();
    }

    @Test
    public void fileReadTest() throws IOException {
        File file = simulateFile(testString);
        Assert.assertEquals(testString, activity.readForTest(file));
    }

    @Test
    public void fileWriteTest() throws IOException {
        File file = simulateFile("");
        activity.saveForTest(file, testString);
        Assert.assertEquals(testString, activity.readForTest(file));
    }

    @Test
    public void fileWriteTestFailed() throws IOException {
        File file = simulateFile(testPassword);
        activity.saveForTest(file, testString);
        Assert.assertEquals(testPassword, activity.readForTest(file));
    }

    @Test
    public void setPasswordTest() throws Exception {
        File file = simulateFile(testPassword);
        activity.setPasswordForTest(file, testPassword);
        String password = activity.readForTest(file);
        Assert.assertEquals(password, testPassword);
    }

    @Test
    public void setPasswordTestFailed() throws IOException {
        File file = simulateFile(testPassword);
        activity.setPasswordForTest(file, testString);
        Assert.assertEquals(testPassword, activity.readForTest(file));
    }
    private File simulateFile(String content) throws IOException {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        File file = new File(Environment.getExternalStorageDirectory(), filename);

        file.createNewFile();

        if(!file.canWrite()) file.setWritable(true);
        assertThat(file.canWrite(), is(true));

        PrintWriter out = new PrintWriter(file);
        out.write(content);
        out.flush();
        out.close();

        return file;
    }
}