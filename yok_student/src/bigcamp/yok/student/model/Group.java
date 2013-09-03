package bigcamp.yok.student.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Group{
	public String name;
	public String id;
	public String teacher;

	@Override
	public String toString() {
		return name;
	}

	public static Group FromJson(JSONObject jsonObject) throws JSONException {
		// [{"teacher_id":1,"name":"이문초등학교 1학년 1반","size_of_team":null,"url":"http://yok-server.cloudapp.net:8080/groups/1.json"}]
		Group group = new Group();
		group.name = jsonObject.getString("name");
		group.teacher = String.valueOf(jsonObject.getInt("teacher_id"));
		return group;
	}
}
