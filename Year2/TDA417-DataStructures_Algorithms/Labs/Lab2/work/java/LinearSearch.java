
// Naive class that just searches linearly through the text

import java.util.List;
import java.util.ArrayList;

class LinearTextSearch {
    String text;

    LinearTextSearch(String text) {
        this.text = text;
    }

    public List<Integer> search(String value, int matches) {
        ArrayList<Integer> result = new ArrayList<>();
        int endpos = this.text.length() - value.length();
        for (int pos = 0; pos < endpos; pos++) {
            if (result.size() > matches) {
                break;
            }
            if (value.equals(this.text.substring(pos, pos+value.length()))) {
                result.add(pos);
            }
        }
        return result;

        // # This is too fast because str.find is implemented in C 
        // # and uses the best string search algorithm.
        //  pos = -1
        //  for _ in range(matches):
        //      pos = self.text.indexOf(value, pos+1)
        //      if pos < 0: 
        //          break
        //      result.append(pos)
        //  return result
    }

}

