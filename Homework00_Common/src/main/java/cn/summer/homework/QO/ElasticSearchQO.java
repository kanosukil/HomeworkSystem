package cn.summer.homework.QO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author VHBin
 * @date 2022/7/27-18:01
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ElasticSearchQO {
    private String value;

    @Override
    public String toString() {
        return value;
    }
}
