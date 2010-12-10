/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.index.core.IndexUtil;

public class PropertyElement extends BaseElement
{
	private static final String EXAMPLES_PROPERTY = "examples"; //$NON-NLS-1$
	private static final String TYPES_PROPERTY = "types"; //$NON-NLS-1$
	private static final String IS_INTERNAL_PROPERTY = "isInternal"; //$NON-NLS-1$
	private static final String IS_INSTANCE_PROPERTY = "isInstanceProperty"; //$NON-NLS-1$
	private static final String IS_CLASS_PROPERTY = "isClassProperty"; //$NON-NLS-1$
	private static final String OWNING_TYPE_PROPERTY = "owningType"; //$NON-NLS-1$

	private String _owningType;
	private boolean _isInstanceProperty;
	private boolean _isClassProperty;
	private boolean _isInternal;
	private List<ReturnTypeElement> _types;
	private List<String> _examples;

	/**
	 * PropertyElement
	 */
	public PropertyElement()
	{
	}

	/**
	 * addExample
	 * 
	 * @param example
	 */
	public void addExample(String example)
	{
		if (example != null && example.length() > 0)
		{
			if (this._examples == null)
			{
				this._examples = new ArrayList<String>();
			}

			this._examples.add(example);
		}
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	public void addType(ReturnTypeElement type)
	{
		if (type != null)
		{
			if (this._types == null)
			{
				this._types = new ArrayList<ReturnTypeElement>();
			}

			int index = this._types.indexOf(type);

			if (index != -1)
			{
				this._types.set(index, type);
			}
			else
			{
				this._types.add(type);
			}
		}
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	public void addType(String type)
	{
		if (type != null && type.length() > 0)
		{
			ReturnTypeElement returnType = new ReturnTypeElement();

			returnType.setType(type);

			this.addType(returnType);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		this.setOwningType(StringUtil.getStringValue(object.get(OWNING_TYPE_PROPERTY)));
		this.setIsClassProperty(Boolean.TRUE == object.get(IS_CLASS_PROPERTY));
		this.setIsInstanceProperty(Boolean.TRUE == object.get(IS_INSTANCE_PROPERTY));
		this.setIsInternal(Boolean.TRUE == object.get(IS_INTERNAL_PROPERTY));

		this._types = IndexUtil.createList(object.get(TYPES_PROPERTY), ReturnTypeElement.class);
		this._examples = IndexUtil.createList(object.get(EXAMPLES_PROPERTY));
	}

	/**
	 * getExamples
	 * 
	 * @return
	 */
	public List<String> getExamples()
	{
		return CollectionsUtil.getListValue(this._examples);
	}

	/**
	 * getOwningType
	 * 
	 * @return
	 */
	public String getOwningType()
	{
		return StringUtil.getStringValue(this._owningType);
	}

	/**
	 * getTypeNames
	 * 
	 * @return
	 */
	public List<String> getTypeNames()
	{
		List<String> result = new ArrayList<String>();

		for (ReturnTypeElement type : this.getTypes())
		{
			result.add(type.getType());
		}

		return result;
	}

	/**
	 * getTypes
	 * 
	 * @return
	 */
	public List<ReturnTypeElement> getTypes()
	{
		return CollectionsUtil.getListValue(this._types);
	}

	/**
	 * isClassProperty
	 * 
	 * @return
	 */
	public boolean isClassProperty()
	{
		return this._isClassProperty;
	}

	/**
	 * isInstanceProperty
	 * 
	 * @return
	 */
	public boolean isInstanceProperty()
	{
		return this._isInstanceProperty;
	}

	/**
	 * isInternal
	 * 
	 * @return
	 */
	public boolean isInternal()
	{
		return this._isInternal;
	}

	/**
	 * setIsClassProperty
	 * 
	 * @param value
	 */
	public void setIsClassProperty(boolean value)
	{
		this._isClassProperty = value;
	}

	/**
	 * setIsInstanceProperty
	 * 
	 * @param value
	 */
	public void setIsInstanceProperty(boolean value)
	{
		this._isInstanceProperty = value;
	}

	/**
	 * setIsInternal
	 * 
	 * @param value
	 */
	public void setIsInternal(boolean value)
	{
		this._isInternal = value;
	}

	/**
	 * setOwningType
	 * 
	 * @param type
	 */
	public void setOwningType(String type)
	{
		this._owningType = type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	@Override
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(OWNING_TYPE_PROPERTY, this.getOwningType());
		out.add(IS_CLASS_PROPERTY, this.isClassProperty());
		out.add(IS_INSTANCE_PROPERTY, this.isInstanceProperty());
		out.add(IS_INTERNAL_PROPERTY, this.isInternal());
		out.add(TYPES_PROPERTY, this.getTypes());
		out.add(EXAMPLES_PROPERTY, this.getExamples());
	}

	/**
	 * toSource
	 * 
	 * @return
	 */
	public String toSource()
	{
		SourcePrinter printer = new SourcePrinter();

		this.toSource(printer);

		return printer.toString();
	}

	/**
	 * toSource
	 * 
	 * @param printer
	 */
	public void toSource(SourcePrinter printer)
	{
		printer.printIndent();

		if (this.isInstanceProperty())
		{
			printer.print("static "); //$NON-NLS-1$
		}
		if (this.isInternal())
		{
			printer.print("internal "); //$NON-NLS-1$
		}

		printer.print(this.getName());
		printer.print(" : "); //$NON-NLS-1$

		List<String> types = this.getTypeNames();

		if (types != null && types.size() > 0)
		{
			printer.print(StringUtil.join(",", this.getTypeNames())); //$NON-NLS-1$
		}
		else
		{
			printer.print(JSTypeConstants.UNDEFINED_TYPE);
		}
	}
}
