package com.meuvesti.cliente.service;

import com.google.gson.JsonObject;
import com.meuvesti.cliente.model.BaseResult;
import com.meuvesti.cliente.model.CatalogResponse;
import com.meuvesti.cliente.model.ConfigurationRequest;
import com.meuvesti.cliente.model.KeywordResponse;
import com.meuvesti.cliente.model.ListBuyResponse;
import com.meuvesti.cliente.model.LoginRequest;
import com.meuvesti.cliente.model.ProductDetail;
import com.meuvesti.cliente.model.User;
import com.meuvesti.cliente.model.UserResult;
import com.meuvesti.cliente.model.Usuario;
import com.meuvesti.cliente.model.ValidateError;
import com.meuvesti.cliente.model.VendaOrcamento;
import com.meuvesti.cliente.model.VendaOrcamentoRequest;
import com.meuvesti.cliente.model.VendaResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hersonrodrigues on 24/01/17.
 */
public interface VestiService {

    @POST("/api/appcompras/login?v=1.0")
    Call<Usuario> login(@Body LoginRequest login);

    @GET("/api/appcompras/lists?v=1.0")
    Call<ListBuyResponse> listsbuy(
            @Query("scheme_url") String schemeUrl
            //@Header("Authorization") String token
    );

    @GET("/api/appcompras/catalogues?v=1.0")
    Call<CatalogResponse> catalogue(
            @Query("perpage") int perPage,
            @Query("keys") String keys,
            @Query("filter_ids") String filter,
            @Query("category_ids") String category_ids,
            @Query("page") int page,
            @Query("scheme_url") String schemeUrl
    );

    @GET("/api/appcompras/listwholesale/{id}/{scheme_url}?v=1.0")
    Call<CatalogResponse> whatsAppList(
            @Path("id") String id,
            @Path("scheme_url") String schemeUrl,
            @Query("page") int page
    );

    @POST("/api/appcompras/quotes?v=1.0")
    Call<VendaResponse> quotes(@Body VendaOrcamentoRequest orcamento);

    @GET("/api/appcompras/products/{id}?v=1.0")
    Call<ProductDetail> detailProduct(
            @Path("id") String id,
            @Query("scheme_url") String scheme
    );

    @GET("/api/appcompras/configs/{scheme_url}?v=1.0")
    Call<ConfigurationRequest> configurations(
            @Path("scheme_url") String scheme
    );

    @PUT("/api/appcompras/domains/{id}?v=1.0")
    Call<UserResult> editiForm(
            @Path("id") String id,
            @Body User user
    );

    @POST("/api/v2/quotes/validate")
    Call<ValidateError> validateProduct(
            @Body VendaOrcamento venda
    );

    @GET("/api/appcompras/validate/document?v=1.0")
    Call<ConfigurationRequest> validaDocumento(
            @Query("document") String document
    );

    @GET("/api/appcompras/validate/email?v=1.0")
    Call<ConfigurationRequest> validaEmail(
            @Query("email") String email
    );

    @POST("/api/appcompras/password/email?v=1.0")
    Call<BaseResult> recoveryPassword(@Body Usuario usuario);

    @POST("/api/appcompras/domains?v=1.0")
    Call<UserResult> cadastro(@Body User usuario, @Query("create") String create);

    @GET("/api/v2/permissions/has/stocks.edit")
    Call<BaseResult> profilePermStockEdit();

    @GET("/api/appcompras/categories?v=1.0")
    Call<JsonObject> categories(
            @Query("category_id") String categoryId,
            @Query("scheme_url") String schemeUrl
    );

    @GET("/api/appcompras/configs/{scheme_url}?v=1.0")
    Call<JsonObject> configs(
            @Path("scheme_url") String schemeUrl
    );

    @GET("/api/appcompras/keywords?v=1.0")
    Call<KeywordResponse> keywords(
            @Query("word") String tagText,
            @Query("scheme_url") String schemeUrl);
}
