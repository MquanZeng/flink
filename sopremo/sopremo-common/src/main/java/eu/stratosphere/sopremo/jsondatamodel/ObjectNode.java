package eu.stratosphere.sopremo.jsondatamodel;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ObjectNode extends JsonNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 222657144282059523L;

	protected LinkedHashMap<String, JsonNode> children = new LinkedHashMap<String, JsonNode>();

	public int size() {
		return this.children.size();
	}

	public ObjectNode put(final String fieldName, final JsonNode value) {
		if (value == null)
			throw new NullPointerException();

		this._put(fieldName, value);
		return this;
	}

	// TODO implement more puts(String, int, double, ...)

	public JsonNode get(final String fieldName) {
		final JsonNode node = this.children.get(fieldName);
		if (node != null)
			return node;
		return NullNode.getInstance();
	}

	public JsonNode remove(final String fieldName) {
		final JsonNode node = this.children.remove(fieldName);
		if (node != null)
			return node;
		return NullNode.getInstance();
	}

	public ObjectNode removeAll() {
		this.children.clear();
		return this;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append('{');

		int count = 0;
		for (final Map.Entry<String, JsonNode> en : this.children.entrySet()) {
			if (count > 0)
				sb.append(',');
			++count;

			TextNode.appendQuoted(sb, en.getKey());
			sb.append(':').append(en.getValue().toString());
		}

		sb.append('}');
		return sb.toString();
	}

	private JsonNode _put(final String fieldName, final JsonNode value) {
		return this.children.put(fieldName, value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.children.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;

		final ObjectNode other = (ObjectNode) obj;
		if (!this.children.equals(other.children))
			return false;
		return true;
	}

	public int getTypePos() {
		return TYPES.ObjectNode.ordinal();
	}

	@Override
	public void read(DataInput in) throws IOException {
		int len = in.readInt();

		for (int i = 0; i < len; i++) {
			JsonNode node;
			String key = in.readUTF();

			try {
				node = TYPES.values()[in.readInt()].getClazz().newInstance();
				node.read(in);
				this.put(key, node);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(this.children.size());

		for (Entry<String, JsonNode> entry : this.children.entrySet()) {
			out.writeUTF(entry.getKey());
			out.writeInt(entry.getValue().getTypePos());
			entry.getValue().write(out);
		}

	}

}
