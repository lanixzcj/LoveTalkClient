package com.example.lovetalk.chat.entity;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.AVUtils;
import com.example.lovetalk.DemoApplication;
import com.example.lovetalk.R;
import com.example.lovetalk.service.ChatService;
import com.example.lovetalk.util.EmotionUtils;
import com.example.lovetalk.util.PathUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lzw on 14-8-7.
 */
public class Msg {
	public static enum Status {
		SendStart(0), SendSucceed(1), SendReceived(2),
		SendFailed(3), HaveRead(4);

		int value;

		Status(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static Status fromInt(int i) {
			return values()[i];
		}
	}

	public static enum Type {
		Text(0), Image(1), Audio(2), Location(3);
		int value;

		Type(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static Type fromInt(int i) {
			return values()[i];
		}
	}

	//long timestamp;
	//String fromPeerId;
	//List<String> toPeerIds;
	//isRequestReceipt
	AVMessage internalMessage;
	String content;
	String objectId;
	String convid;


	RoomType roomType = RoomType.Single;
	Status status = Status.SendStart;
	Type type = Type.Text;


	public Msg() {
		internalMessage = new AVMessage();
	}

	public AVMessage getInternalMessage() {
		return internalMessage;
	}

	public void setInternalMessage(AVMessage internalMessage) {
		this.internalMessage = internalMessage;
	}

	public String getToPeerId() {
		List<String> toPeerIds = internalMessage.getToPeerIds();
		if (toPeerIds != null && toPeerIds.size() > 0) {
			return toPeerIds.get(0);
		} else {
			return convid;
		}
	}

	private List<String> getToPeerIds() {
		return internalMessage.getToPeerIds();
	}

	public void setToPeerId(String toPeerId) {
		setToPeerIds(Arrays.asList(toPeerId));
	}

	private void setToPeerIds(List<String> toPeerIds) {
		internalMessage.setToPeerIds(toPeerIds);
	}

	public String getFromPeerId() {
		return internalMessage.getFromPeerId();
	}

	public void setFromPeerId(String fromPeerId) {
		internalMessage.setFromPeerId(fromPeerId);
	}

	public long getTimestamp() {
		return internalMessage.getTimestamp();
	}

	public boolean isRequestReceipt() {
		return internalMessage.isRequestReceipt();
	}

	public void setRequestReceipt(boolean isRequestReceipt) {
		internalMessage.setRequestReceipt(isRequestReceipt);
	}

	public String getConvid() {
		return convid;
	}

	public void setConvid(String convid) {
		this.convid = convid;
	}

	public RoomType getRoomType() {
		return roomType;
	}

	public void setRoomType(RoomType roomType) {
		this.roomType = roomType;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setTimestamp(long timestamp) {
		internalMessage.setTimestamp(timestamp);
	}

	public String getContent() {
		return content;
	}

	public String getStatusDesc() {
		if (status == Status.SendStart) {
			return DemoApplication.context.getString(R.string.sending);
		} else if (status == Status.SendReceived) {
			return DemoApplication.context.getString(R.string.received);
		} else if (status == Status.SendSucceed) {
			return DemoApplication.context.getString(R.string.sent);
		} else if (status == Status.SendFailed) {
			return DemoApplication.context.getString(R.string.failed);
		} else if (status == Status.HaveRead) {
			return DemoApplication.context.getString(R.string.haveRead);
		} else {
			throw new IllegalArgumentException("unknown status");
		}
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public boolean isComeMessage() {
		String fromPeerId = getFromPeerId();
		return !fromPeerId.equals(ChatService.getSelfId());
	}

	public String getOtherId() {
		if (getRoomType() == RoomType.Group) {
			return getToPeerId();
		} else {
			String fromPeerId = getFromPeerId();
			String selfId = ChatService.getSelfId();
			if (fromPeerId.equals(selfId)) {
				return getToPeerId();
			} else {
				return fromPeerId;
			}
		}
	}

	public String getFromName() {
		String peerId = getFromPeerId();
		AVUser user = DemoApplication.lookupUser(peerId);
		return user.getUsername();
	}

	public CharSequence getNotifyContent() {
		switch (type) {
			case Audio:
				return DemoApplication.context.getString(R.string.audio);
			case Text:
				if (EmotionUtils.haveEmotion(getContent())) {
					return DemoApplication.context.getString(R.string.emotion);
				} else {
					return getContent();
				}
			case Image:
				return DemoApplication.context.getString(R.string.image);
			case Location:
				return DemoApplication.context.getString(R.string.position);
			default:
				return DemoApplication.context.getString(R.string.newMessage);
		}
	}

	public static Msg fromAVMessage(AVMessage avMsg) {
		Msg msg = new Msg();
		msg.setInternalMessage(avMsg);
		if (!AVUtils.isBlankString(avMsg.getMessage())) {
			HashMap<String, Object> params = JSON.parseObject(avMsg.getMessage(), HashMap.class);
			String objectId = (String) params.get("objectId");
			String content = (String) params.get("content");
			Integer typeInt = (Integer) params.get("type");
			if (objectId == null || content == null ||
					typeInt == null) {
				throwNullException();
			}
			msg.setObjectId(objectId);
			msg.setContent(content);
			Type type = Type.fromInt(typeInt);
			msg.setType(type);
		}
		return msg;
	}

	public AVMessage toAVMessage() {
		if (convid == null || content == null || objectId == null
				|| roomType == null || status == null || type == null) {
			throwNullException();
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("objectId", objectId);
		params.put("content", content);
		params.put("type", type.getValue());
		internalMessage.setMessage(JSON.toJSONString(params));
		return internalMessage;
	}

	public static void throwNullException() {
		throw new NullPointerException("at least one of these is null: content,objectId,type");
	}

	@Override
	public String toString() {
		return "{content:" + getContent() + " objectId:" + getObjectId() + " status:" + getStatus() + " fromPeerId:" +
				getFromPeerId() + " toPeerIds:" + getToPeerIds()
				+ " timestamp:" + getTimestamp() + " type=" + getType() + "}";
	}

	public String getAudioPath() {
		return PathUtils.getChatFileDir() + getObjectId();
	}


}
