package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.test.mock.MockContext;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.tournaments.Match;
import com.mattkormann.tournamentmanager.tournaments.StandardMatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Created by Matt on 6/27/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MatchBracketHolderTest {

    @Mock
    Context mContext;

    TextView mTextView;

    public MatchBracketHolderTest() {
        mTextView = new TextView(mContext);
    }

    @Test
    public void testTextChanges() {
        TestObject testObject = new TestObject("Joey");
        mTextView.setText(testObject.getName());
        assertEquals("Joey", mTextView.getText().toString());
        testObject = new TestObject("Mikey");
        assertEquals("Mikey", mTextView.getText().toString());
    }

    class TestObject {

        private String name;

        public TestObject(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
