// Copyright (c) 2007-2013 Amazon.com, Inc.  All rights reserved.

package com.amazon.ion;


/**
 * Base type for all Ion data nodes.
 * <p>
 * <b>WARNING:</b> This interface should not be implemented or extended by
 * code outside of this library.
 * <p>
 * The {@code IonValue} hierarchy presents a "tree view" of Ion data;
 * every node in the tree is an instance of this class.  Since the Ion
 * type system is highly orthogonal, most operations use this
 * base type, and applications will need to examine individual instances and
 * "downcast" the value to one of the "real" types (<em>e.g.</em>,
 * {@link IonString}) in order to access the Ion content.
 * <p>
 * Besides the real types, there are other generic interfaces that can be
 * useful:
 * <ul>
 *   <li>
 *     {@link IonText} generalizes {@link IonString} and {@link IonSymbol}
 *   </li>
 *   <li>
 *     {@link IonContainer} generalizes
 *     {@link IonList}, {@link IonSexp}, and {@link IonStruct}
 *   </li>
 *   <li>
 *     {@link IonSequence} generalizes {@link IonList} and {@link IonSexp}
 *   </li>
 *   <li>
 *     {@link IonLob} generalizes {@link IonBlob} and {@link IonClob}
 *   </li>
 * </ul>
 * <p>
 * To determine the real type of a generic {@code IonValue}, there are three
 * main mechanisms:
 * <ul>
 *   <li>
 *     Use {@code instanceof} to look for a desired interface:
 * <pre>
 *    if (v instanceof IonString)
 *    {
 *        useString((IonString) v);
 *    }
 *    else if (v instanceof IonStruct)
 *    {
 *        useStruct((IonStruct) v);
 *    }
 *    // ...
 * </pre>
 *   </li>
 *   <li>
 *     Call {@link #getType()} and then {@code switch} over the resulting
 *     {@link IonType}:
 * <pre>
 *    switch (v.getType())
 *    {
 *        case IonType.STRING: useString((IonString) v); break;
 *        case IonType.STRUCT: useStruct((IonStruct) v); break;
 *        // ...
 *    }
 * </pre>
 *   </li>
 *   <li>
 *     Implement {@link ValueVisitor} and call {@link #accept(ValueVisitor)}:
 * <pre>
 *    public class MyVisitor
 *        extends AbstractValueVisitor
 *    {
 *        public void visit(IonString value)
 *        {
 *            useString(v);
 *        }
 *        public void visit(IonStruct value)
 *        {
 *            useStruct(v);
 *        }
 *        // ...
 *     }
 * </pre>
 *   </li>
 * </ul>
 * Use the most appropriate mechanism for your algorithm, depending upon how
 * much validation you've done on the data.
 * <p>
 * <b>Mutable instances of {@code IonValue} are not thread-safe!</b>
 * Your application must perform its own synchronization if you need to access
 * nodes from multiple threads. This is true even for read-only use cases,
 * since implementations may perform lazy materialization or other state
 * changes under the covers.  Alternatively, you can invoke
 * {@link #makeReadOnly()} from a single thread, after which point the value
 * (and all contained values, recursively) will be both immutable and
 * thread-safe.
 */
public interface IonValue
    extends Cloneable
{
    /**
     * A zero-length immutable {@code IonValue} array.
     */
    public static final IonValue[] EMPTY_ARRAY = new IonValue[0];


    /**
     * Gets an enumeration value identifying the core Ion data type of this
     * object.
     *
     * @return a non-<code>null</code> enumeration value.
     */
    public IonType getType();


    /**
     * Determines whether this in an Ion null value, <em>e.g.</em>,
     * <code>null</code> or <code>null.string</code>.
     * Note that there are unique null values for each Ion type.
     *
     * @return <code>true</code> if this value is one of the Ion null values.
     */
    public boolean isNullValue();


    /**
     * Determines whether this value is read-only.  Such values are safe for
     * simultaneous read from multiple threads.
     *
     * @return <code>true</code> if this value is read-only and safe for
     * multi-threaded reads.
     *
     * @see #makeReadOnly()
     */
    public boolean  isReadOnly();


    /**
     * Gets the symbol table used to encode this value.  The result is either a
     * local or system symbol table (or null).
     *
     * @return the symbol table, or <code>null</code> if this value is not
     * currently backed by binary-encoded data.
     */
    public SymbolTable getSymbolTable();


    /**
     * Gets the field name attached to this value,
     * or <code>null</code> if this is not part of an {@link IonStruct}.
     *
     * @throws UnknownSymbolException if the field name has unknown text.
     */
    public String getFieldName();


    /**
     * Gets the field name attached to this value as an interned symbol
     * (text + ID).
     *
     * @return null if this value isn't a struct field.
     *
     * @since IonJava R15
     */
    public SymbolToken getFieldNameSymbol();


    /**
     * Gets the symbol ID of the field name attached to this value.
     *
     * @return the symbol ID of the field name, if this is part of an
     * {@link IonStruct}. If this is not a field, or if the symbol ID cannot be
     * determined, this method returns a value <em>less than one</em>.
     *
     * @deprecated Since IonJava R15. Use {@link #getFieldNameSymbol()} instead.
     */
    @Deprecated
    public int getFieldId();


    /**
     * Gets the container of this value,
     * or <code>null</code> if this is not part of one.
     */
    public IonContainer getContainer();


    /**
     * Removes this value from its container, if any.
     *
     * @return {@code true} if this value was in a container before this method
     * was called.
     */
    public boolean removeFromContainer();


    /**
     * Finds the top level value above this value.
     * If this value has no container, or if it's immediate container is a
     * datagram, then this value is returned.
     *
     * @return the top level value above this value, never null, and never an
     * {@link IonDatagram}.
     *
     * @throws UnsupportedOperationException if this is an {@link IonDatagram}.
     *
     * @since IonJava R15
     */
    public IonValue topLevelValue();


    /**
     * Gets this value's user type annotations as text.
     *
     * @return the (ordered) annotations on the current value, or an empty
     * array (not {@code null}) if there are none.
     *
     * @throws UnknownSymbolException if any annotation has unknown text.
     */
    public String[] getTypeAnnotations();


    /**
     * Gets this value's user type annotations as interned symbols (text + ID).
     *
     * @return the (ordered) annotations on the current value, or an empty
     * array (not {@code null}) if there are none.
     *
     * @since IonJava R15
     */
    public SymbolToken[] getTypeAnnotationSymbols();


    /**
     * Determines whether or not the value is annotated with
     * a particular user type annotation.
     * @param annotation as a string value.
     * @return <code>true</code> if this value has the annotation.
     */
    public boolean hasTypeAnnotation(String annotation);


    /**
     * Replaces all type annotations with the given text.
     *
     * @param annotations the new annotations.  If null or empty array, then
     *  all annotations are removed.  Any duplicates are preserved.
     *
     * @throws EmptySymbolException if any of the annotations are null or
     *  empty string.
     *
     * @since IonJava R13
     */
    public void setTypeAnnotations(String... annotations);

    /**
     * Replaces all type annotations with the given symbol tokens.
     * The contents of the {@code annotations} array are copied into this
     * writer, so the caller does not need to preserve the array.
     * <p>
     * <b>This is an "expert method": correct use requires deep understanding
     * of the Ion binary format. You almost certainly don't want to use it.</b>
     *
     * @param annotations the new annotations.
     * If null or empty array, then all annotations are removed.
     * Any duplicates are preserved.
     *
     * @since IonJava R15
     */
    public void setTypeAnnotationSymbols(SymbolToken... annotations);

    /**
     * Removes all the user type annotations attached to this value.
     */
    public void clearTypeAnnotations();


    /**
     * Adds a user type annotation to the annotations attached to
     * this value.  If the annotation exists the list does not change.
     * @param annotation as a string value.
     */
    public void addTypeAnnotation(String annotation);


    /**
     * Removes a user type annotation from the list of annotations
     * attached to this value.
     * If the annotation appears more than once, only the first occurrance is
     * removed.
     * If the annotation does not exist, the value does not change.
     *
     * @param annotation as a string value.
     *  If null or empty, the method has no effect.
     */
    public void removeTypeAnnotation(String annotation);


    /**
     * Copies this value to the given {@link IonWriter}.
     * <p>
     * This method writes annotations and field names (if in a struct),
     * and performs a deep write, including the contents of
     * any containers encountered.
     *
     * @since IonJava R13
     */
    public void writeTo(IonWriter writer);


    /**
     * Entry point for visitor pattern.  Implementations of this method by
     * concrete classes will simply call the appropriate <code>visit</code>
     * method on the <code>visitor</code>.  For example, instances of
     * {@link IonBool} will invoke {@link ValueVisitor#visit(IonBool)}.
     *
     * @param visitor will have one of its <code>visit</code> methods called.
     * @throws Exception any exception thrown by the visitor is propagated.
     * @throws NullPointerException if <code>visitor</code> is
     * <code>null</code>.
     */
    public void accept(ValueVisitor visitor) throws Exception;


    /**
     * Marks this instance and its children to be immutable.
     * In addition, read-only values are safe for simultaneous use
     * from multiple threads.  This may require materializing the Java
     * forms of the values.
     * <p>
     * After this method completes, any attempt to change the state of this
     * instance, or of any contained value, will trigger a
     * {@link ReadOnlyValueException}.
     *
     * @see #isReadOnly()
     */
    public void makeReadOnly();


    /**
     * Gets the system that constructed this value.
     *
     * @return not null.
     */
    public IonSystem getSystem();


    /**
     * Creates a copy of this value and all of its children. The cloned value
     * may use the same shared symbol tables, but it will have an independent local
     * symbol table if necessary.  The cloned value will
     * be modifiable regardless of whether this instance {@link #isReadOnly()}.
     * <p>
     * The cloned value will be created in the context of the same
     * {@link ValueFactory} as this instance; if you want a copy using a
     * different factory, then use {@link ValueFactory#clone(IonValue)}
     * instead.
     *
     * @throws UnknownSymbolException
     *          if any part of this value has unknown text but known Sid for
     *          its field name, annotation or symbol.
     */
    public IonValue clone()
        throws UnknownSymbolException;


    /**
     * Returns a <em>non-canonical</em> ASCII representation of this value.
     * All data will be on a single line, with minimal whitespace.
     * There is no guarantee that multiple invocations of this method will
     * return identical results, only that they will be equivalent per
     * the Ion data model.
     * <p>
     * For more configurable rendering, see
     * {@link com.amazon.ion.system.IonTextWriterBuilder}.
     *
     * @return Ion text data equivalent to this value.
     */
    public String toString();


    /**
     * Compares two Ion values for structural equality, which means that they
     * represent the exact same semantics, including annotations, numeric
     * precision, and so on.  This is a "deep" comparison that recursively
     * traverse the hierarchy, and as such it should be considered an expensive
     * operation.
     *
     * @see com.amazon.ion.util.Equivalence
     *
     * @param   other   The value to compare with.
     *
     * @return  A boolean, true if the argument is an {@link IonValue} that
     *   is semantically identical within the Ion data model, including
     *   precision and annotations.
     */
    public boolean equals(Object other);


    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     * <p>
     * {@inheritDoc}
     */
    public int hashCode();
}
