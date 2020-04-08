package com.base.httpmvp.retrofitapi.converter;


import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.methods.HttpState;
import com.base.httpmvp.retrofitapi.exception.ApiException;
import com.base.httpmvp.retrofitapi.exception.TokenInvalidException;
import com.base.httpmvp.retrofitapi.exception.TokenNotExistException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by BYPC006 on 2017/3/6.  自定义转换器
 */

public class ListConverterFactory extends Converter.Factory {
	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
		//根据type判断是否是自己能处理的类型，不能的话，return null ,交给后面的Converter.Factory
		return new ListResponseConverter<>(type);
	}

	public class ListResponseConverter<T> implements Converter<ResponseBody, T>
	{
		private Type type;
		// 解决data返回为字符串""时跳出的异常
		Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(List.class, new JsonDeserializer<List<?>>() {
			@Override
			public List<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				if (json.isJsonArray()) {
					JsonArray array = json.getAsJsonArray();
					Type itemType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
					List list = new ArrayList<>();
					for (int i = 0; i < array.size(); i++) {
						JsonElement element = array.get(i);
						Object item = context.deserialize(element, itemType);
						list.add(item);
					}
					return list;
				} else {
					//和接口类型不符，返回空List
					return Collections.EMPTY_LIST;
				}
			}
		}).registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory<>()).create();

		public ListResponseConverter(Type type)
		{
			this.type = type;
		}

		@Override
		public T convert(ResponseBody responseBody) throws IOException
		{
			String result = responseBody.string();
			HttpState httpState = gson.fromJson(result, HttpState.class);
			if (!httpState.isReturnState()) {
				if (httpState.getMessage().equals(HttpCode.TOKEN_NOT_EXIST)) {
					throw new TokenNotExistException();
				} else if (httpState.getMessage().equals(HttpCode.TOKEN_INVALID)) {
					throw new TokenInvalidException();
				} else {
					// 特定 API 的错误，在相应的 Subscriber 的 onError 的方法中进行处理
					throw new ApiException(httpState.getMessage());
				}
			}
			T users = gson.fromJson(result, type);
			return users;
		}
	}

	@Override
	public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
		return new ListRequestBodyConverter<>();
	}
	public class ListRequestBodyConverter<T> implements Converter<T, RequestBody> {
		private Gson mGson = new Gson();
		@Override
		public RequestBody convert(T value) throws IOException {
			String string = mGson.toJson(value);
			return RequestBody.create(MediaType.parse("application/json; charset=UTF-8"),string);
		}
	}
	public class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
		@SuppressWarnings("unchecked")
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			Class<T> rawType = (Class<T>) type.getRawType();
			if (rawType != String.class) {
				return null;
			}
			return (TypeAdapter<T>) new StringNullAdapter();
		}
	}

	public class StringNullAdapter extends TypeAdapter<String> {
		@Override
		public String read(JsonReader reader) throws IOException {
			// TODO Auto-generated method stub
			if (reader.peek() == JsonToken.NULL) {
				reader.nextNull();
				return "";
			}
			return reader.nextString();
		}
		@Override
		public void write(JsonWriter writer, String value) throws IOException {
			// TODO Auto-generated method stub
			if (value == null) {
				writer.nullValue();
				return;
			}
			writer.value(value);
		}
	}
}
