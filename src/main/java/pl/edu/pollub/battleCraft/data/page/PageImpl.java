package pl.edu.pollub.battleCraft.data.page;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PageImpl<T> implements Page<T> {
    private final long totalElements;
    private final List<T> content;
    private final Pageable pageable;

    public PageImpl(List<T> content, long totalElements, Pageable pageable){
        this.content = content;
        this.totalElements = totalElements;
        this.pageable = pageable;
    }

    @Override
    public int getTotalPages() {
        return getSize() == 0 ? 1 : (int) Math.ceil((double) totalElements / (double) getSize());
    }

    @Override
    public long getTotalElements() {
        return totalElements;
    }

    @Override
    public int getNumber() {
        return pageable.getPageNumber();
    }

    @Override
    public int getSize() {
        return content.size();
    }

    @Override
    public int getNumberOfElements() {
        return pageable.getPageSize();
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public boolean hasContent() {
        return content.size()>0;
    }

    @Override
    public Sort getSort() {
        return pageable.getSort();
    }

    @Override
    public boolean isFirst() {
        return !hasPrevious();
    }

    @Override
    public boolean isLast() {
        return !hasNext();
    }

    @Override
    public boolean hasNext() {
        return getNumber()+ 1<totalElements;
    }

    @Override
    public boolean hasPrevious() {
        return getNumber() > 0;
    }

    @Override
    public Pageable nextPageable() {
        return hasNext() ? pageable.next() : null;
    }

    @Override
    public Pageable previousPageable() {
        return pageable.previousOrFirst();
    }

    @Override
    public <S> Page<S> map(Converter<? super T, ? extends S> converter) {
        return new org.springframework.data.domain.PageImpl<S>(getConvertedContent(converter), pageable, getTotalPages());
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }

    private <S> List<S> getConvertedContent(Converter<? super T, ? extends S> converter) {

        Assert.notNull(converter, "Converter must not be null!");

        List<S> result = new ArrayList<S>(content.size());

        for (T element : this) {
            result.add(converter.convert(element));
        }

        return result;
    }
}
