package com.meuvesti.cliente.realm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meuvesti.cliente.model.Color;
import com.meuvesti.cliente.model.Image;
import com.meuvesti.cliente.model.ItemProduct;
import com.meuvesti.cliente.model.Pack;
import com.meuvesti.cliente.model.PackItem;
import com.meuvesti.cliente.model.Photo;
import com.meuvesti.cliente.model.ProductColor;
import com.meuvesti.cliente.model.ProductDetailItem;
import com.meuvesti.cliente.model.SizeItem;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by hersonrodrigues on 02/02/17.
 */
public class CartRealmService {
    // Adiciona um novo produto no carrinho
    public static void addToCart(final ProductRealm product) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                product.setInCart(true);
                realm.copyToRealmOrUpdate(product);
            }
        });
        realm.close();
    }

    // Remove um produto do carrinho
    public static void removeFromCart(final String id) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ProductRealm item = realm.where(ProductRealm.class).equalTo("id", id).findFirst();
                item.setInCart(false);
            }
        });
        realm.close();
    }

    /*
        // Lista os produtos adicionados no carrinho

        VITOR UTLIZAR ESSE METODO:
        Ex: CartRealmService.getProductsInCart();

     */
    public static RealmResults<ProductRealm> getProductsInCart() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ProductRealm> list = realm.where(ProductRealm.class).equalTo("inCart", true).findAll();
        return list;
    }

    // Lista os produtos adicionados no carrinho
    public static int getTotalProductsInCart() {
        return getProductsInCart().size();
    }

    public static boolean hasProduct(String id) {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<ProductRealm> list = realm.where(ProductRealm.class)
                .equalTo("id", id)
                .equalTo("inCart", true)
                .findAll();

        if (list.size() >= 1) {
            return true;
        } else {
            return false;
        }
    }

    public static int totalByProduct(ProductRealm mProduct) {
        int total = 0;

        // GRADE ABERTA
        for (PackRealm pack : mProduct.getPacks()) {
            if (mProduct.getPtype().equalsIgnoreCase("1") || mProduct.getPtype().equalsIgnoreCase("2")) {
                for (PackItemRealm packItem : pack.getItens()) {
                    if (packItem.isValid())
                        for (SizeItemRealm size : packItem.getSizes()) {
                            SizeItemRealm item = size;
                            total = total + item.getQuantity();
                        }
                }
            }
        }

        int totalGeralPack = 0;

        // GRADE FECHADA
        for (PackRealm pack : mProduct.getPacks()) {
            if (mProduct.getPtype().equalsIgnoreCase("3") || mProduct.getPtype().equalsIgnoreCase("4")) {

                int totalInPack = 0;

                for (PackItemRealm packItem : pack.getItens()) {
                    if (packItem.getQuantityPacks() != 0) {
                        for (SizeItemRealm pmr : packItem.getSizes()) {
                            totalInPack = totalInPack + pmr.getQuantity();
                        }
                        totalInPack = totalInPack * packItem.getQuantityPacks();
                    }
                }
                totalGeralPack = totalGeralPack + totalInPack;
            }
        }

        return total + totalGeralPack;
    }

    public static ProductRealm getProductById(String id) {
        Realm realm = Realm.getDefaultInstance();
        ProductRealm item = realm.where(ProductRealm.class).equalTo("id", id).equalTo("inCart", true).findFirst();
        return item;
    }

    public static List<String> photoToString(List<Photo> imagem) {
        List<String> fotos = new ArrayList<>();
        for (Photo url : imagem) {
            if (url.getUrl().endsWith(".jpg") || url.getUrl().endsWith(".jpeg")) {
                fotos.add(url.getUrl());
            } else {
                fotos.add(url.getUrl() + url.getUrlLarge());
            }
        }
        return fotos;
    }

    public static List<String> photoHDToString(List<Photo> imagem) {
        List<String> fotos = new ArrayList<>();
        for (Photo url : imagem) {
            if (url.getUrl().endsWith(".jpg") || url.getUrl().endsWith(".jpeg")) {
                fotos.add(url.getUrl());
            } else {
                fotos.add(url.getUrl() + url.getUrlLarge());
            }
        }
        return fotos;
    }

    public static RealmList<PhotoRealm> imageToRealm(List<Image> fotos) {
        RealmList<PhotoRealm> fotosRealm = new RealmList<>();
        for (Image image : fotos) {
            fotosRealm.add(new PhotoRealm(image.getUrlOrig()));
        }
        return fotosRealm;
    }


    public static RealmList<PhotoRealm> photoToRealm(List<Photo> fotos) {
        RealmList<PhotoRealm> fotosRealm = new RealmList<>();
        for (String url : photoToString(fotos)) {
            fotosRealm.add(new PhotoRealm(url));
        }
        return fotosRealm;
    }

    public static ProductRealm objectToRealm(ItemProduct itemProduct, ProductDetailItem p) {
        ProductRealm pr = new ProductRealm();
        pr.setInCart(false);
        pr.setId(p.getId());
        pr.setNome(p.getNome());
        pr.setCodigo(p.getCodigo());
        pr.setComposicao(p.getComposicao());
        pr.setDescricao(p.getDescricao());
        pr.setMarca(itemProduct.getBrandName());
        pr.setValor(p.getValor());

        if (p.getImages() != null && !p.getImages().isEmpty()) {
            pr.setImagem(CartRealmService.imageToRealm(p.getImages()));
        } else {
            pr.setImagem(CartRealmService.photoToRealm(p.getImagem()));
        }

        pr.setAtivo(p.isAtivo());
        pr.setPtype(p.getPtype());

        RealmList<PackRealm> realmPackList = new RealmList<>();
        if (p.getColors() != null && !p.getColors().isEmpty()) {
            if (p.getColors() != null && !p.getColors().isEmpty()) {
                RealmList<ColorRealm> colorReamList = new RealmList<>();
                for (ProductColor color : p.getColors()) {
                    ColorRealm cr = new ColorRealm();
                    cr.setName(color.getName());
                    cr.setCode(color.getCode());
                    cr.setEstampa(color.getEstampa());
                    cr.setId(color.getId());
                    cr.setPhoto(color.getPhoto());
                    colorReamList.add(cr);
                }
                pr.setColors(colorReamList);
            }
        }
        if (p.getPacks() != null) {
            for (Pack pack : p.getPacks()) {

                PackRealm ppr = new PackRealm();

                RealmList<PackItemRealm> packItensRealm = new RealmList<>();
                if (pack.getItens() != null) {
                    for (PackItem pi : pack.getItens()) {

                        PackItemRealm pir = new PackItemRealm();
                        ColorRealm color = new ColorRealm();
                        color.setId(pi.getColor().getId());
                        pir.setColor(color);

                        RealmList<SizeItemRealm> pmrList = new RealmList<>();
                        RealmList<SizeItemRealm> pmrList2 = new RealmList<>();

                        if (pi.getSizes() != null) {
                            for (SizeItem pis : pi.getSizes()) {
                                if (pis != null) {
                                    SizeItemRealm sir = new SizeItemRealm();
                                    sir.setQuantity(pis.getQuantity());
                                    sir.setSell(pis.isSell());
                                    sir.setKey(pis.getKey());
                                    pmrList.add(sir);
                                }
                            }
                        }
                        // Corrige ordem dos PMG
                        if (pmrList.size() >= 0) {
                            for (SizeItemRealm pmrItem : pmrList) {
                                pmrList2.add(pmrItem);
                            }
                        }
                        pir.setSizes(pmrList2);
                        packItensRealm.add(pir);
                    }
                }

                ppr.setItens(packItensRealm);
                realmPackList.add(ppr);
            }
        }
        pr.setPacks(realmPackList);

        return pr;
    }

    public static ProductDetailItem realmToObject(ProductRealm p) {
        ProductDetailItem pdi = new ProductDetailItem();

        pdi.setAtivo(true);
        pdi.setCodigo(p.getCodigo());
        pdi.setComposicao(p.getComposicao());
        pdi.setDescricao(p.getDescricao());
        pdi.setMarca(p.getMarca());
        pdi.setValor(p.getValor());
        pdi.setNome(p.getNome());

        List<Photo> fotos = new ArrayList<>();
        for (PhotoRealm pr : p.getImagem()) {

            Photo f = new Photo();
            f.setUrlSmall(pr.getUrl());
            f.setUrlLarge(pr.getUrl());

            fotos.add(f);
        }

        pdi.setImagem(fotos);

        List<Pack> packs = new ArrayList<>();
        for (PackRealm packRealm : p.getPacks()) {
            Pack pack = new Pack();

            List<PackItem> packItems = new ArrayList<>();

            RealmList<PackItemRealm> packRealmList = packRealm.getItens();
            for (PackItemRealm pir : packRealmList) {

                List<SizeItem> listSizes = new ArrayList<>();
                RealmList<SizeItemRealm> sizes = pir.getSizes();
                for (SizeItemRealm pmr : sizes) {
                    String key = pmr.getKey();
                    SizeItemRealm value = pmr;

                    SizeItem sizeItem = new SizeItem();
                    sizeItem.setQuantity(value.getQuantity());
                    sizeItem.setSell(value.isSell());
                    sizeItem.setKey(key);

                    listSizes.add(sizeItem);
                }

                /*ProductColor currentColor;
                for(ProductColor cor : pack.getColors()){
                    if(cor.equals(pir.getColorId())) {
                        currentColor = cor;
                    }
                }*/

                PackItem packItem = new PackItem();
                //packItem.setCode(currentColor.getCode());
                //packItem.setName(pir.getName());
                //packItem.setEstampa(pir.getEstampa());
                //packItem.setColorId(pir.getColorId());
                packItem.setSizes(listSizes);
                packItems.add(packItem);
            }

            pack.setItens(packItems);
        }

        pdi.setPacks(packs);
        return pdi;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(ProductRealm.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }
}
