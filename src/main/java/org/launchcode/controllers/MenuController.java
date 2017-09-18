package org.launchcode.controllers;

import org.launchcode.models.Menu;
import org.launchcode.models.Cheese;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CategoryDao categoryDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "My Menus");

        return "menu/index";

    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model,
                      @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:/menu/view/" + menu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id) {

        Menu menu = menuDao.findOne(id);

        model.addAttribute("title", menu.getName());
        model.addAttribute("menu", menuDao.findOne(id));

        return "menu/view";


    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId, Menu menu, Errors errors) {

        AddMenuItemForm newMenuItemForm = new AddMenuItemForm(menu, cheeseDao.findAll());
        Menu menuToEdit = menuDao.findOne(menuId);

        model.addAttribute("title", "Add item to menu:" + menuToEdit.getName());
        model.addAttribute("form", newMenuItemForm);
        model.addAttribute("menu", menuToEdit);


        return "menu/add-item";


    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.POST)
    public String addItem(Model model, @PathVariable int menuId, AddMenuItemForm form, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("form", form);
            return "menu/add-item";
        } else {

            Cheese theCheese = cheeseDao.findOne(form.getCheeseId());
            Menu theMenu = menuDao.findOne(menuId);

            theMenu.addItem(theCheese);

            menuDao.save(theMenu);

            return "redirect:/menu/view/" + menuId;
        }
    }

}
