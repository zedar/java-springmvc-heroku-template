package api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString(includeFieldNames=true)
public class Action {
  @Getter @Setter private String id;
  @Getter @Setter private String name;
  @Getter @Setter private String description;
  @Getter @Setter private String url;
  @Getter @Setter private String tags;
}
