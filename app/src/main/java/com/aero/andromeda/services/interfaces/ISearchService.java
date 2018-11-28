package com.aero.andromeda.services.interfaces;

import com.aero.andromeda.models.AppMenuItem;
import java.util.List;

public interface ISearchService
{
	void search(String text);
	List<AppMenuItem> getSearchResultList();
}
