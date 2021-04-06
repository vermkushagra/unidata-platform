/**
 *
 */

package com.unidata.mdm.backend.service.job.batch.core.configuration.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.configuration.support.AbstractApplicationContextFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class CustomApplicationContextFactory extends AbstractApplicationContextFactory {
    private ClassLoader customClassLoader;

	/**
	 * Create an application context factory for the resource specified. The resource can be an actual {@link Resource},
	 * in which case it will be interpreted as an XML file, or it can be a &#64;Configuration class, or a package name.
	 * All types must be the same (mixing XML with a java package for example is not allowed and will result in an
	 * {@link java.lang.IllegalArgumentException}).
	 *
	 * @param resources some resources (XML configuration files, &#064;Configuration classes or java packages to scan)
	 */
	public CustomApplicationContextFactory(Object... resources) {
		super(resources);
        customClassLoader = null;
	}

	public CustomApplicationContextFactory(ClassLoader classLoader, Object... resources) {
		super(resources);
        customClassLoader = classLoader;
	}

	/**
	 * @see AbstractApplicationContextFactory#createApplicationContext(ConfigurableApplicationContext, Object...)
	 */
	@Override
	protected ConfigurableApplicationContext createApplicationContext(ConfigurableApplicationContext parent,
			Object... resources) {
		ConfigurableApplicationContext context;

		if (allObjectsOfType(resources, Resource.class)) {
			 context = new ResourceXmlApplicationContext(parent, resources);
		} else if (allObjectsOfType(resources, Class.class)) {
			 context =  new ResourceAnnotationApplicationContext(parent, resources);
		} else if (allObjectsOfType(resources, String.class)) {
			 context = new ResourceAnnotationApplicationContext(parent, resources);
		} else {
			List<Class<?>> types = new ArrayList<>();
			for (Object resource : resources) {
				types.add(resource.getClass());
			}
			throw new IllegalArgumentException("No application context could be created for resource types: "
													   + Arrays.toString(types.toArray()));
		}

		return context;
	}

	private boolean allObjectsOfType(Object[] objects, Class<?> type) {
		for (Object object : objects) {
			if (!type.isInstance(object)) {
				return false;
			}
		}
		return true;
	}

	private abstract class ApplicationContextHelper {

		private final DefaultListableBeanFactory parentBeanFactory;

		private final ConfigurableApplicationContext parent;

		public ApplicationContextHelper(ConfigurableApplicationContext parent, GenericApplicationContext context,
				Object... config) {
			this.parent = parent;
			if (parent != null) {
				Assert.isTrue(parent.getBeanFactory() instanceof DefaultListableBeanFactory,
                    "The parent application context must have a bean factory of type DefaultListableBeanFactory");
				parentBeanFactory = (DefaultListableBeanFactory) parent.getBeanFactory();
			}
			else {
				parentBeanFactory = null;
			}
			context.setParent(parent);
			context.setId(generateId(config));
			loadConfiguration(config);
			prepareContext(parent, context);
		}

		protected abstract String generateId(Object... configs);

		protected abstract void loadConfiguration(Object... configs);

		protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
			if (parentBeanFactory != null) {
				CustomApplicationContextFactory.this.prepareBeanFactory(parentBeanFactory, beanFactory);
				AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
				autowiredAnnotationBeanPostProcessor.setBeanFactory(beanFactory);
				beanFactory.addBeanPostProcessor(autowiredAnnotationBeanPostProcessor);
				if(beanFactory instanceof DefaultListableBeanFactory){
					((DefaultListableBeanFactory) beanFactory).setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
				}

				for (Class<? extends BeanFactoryPostProcessor> cls : getBeanFactoryPostProcessorClasses()) {
					for (String name : parent.getBeanNamesForType(cls)) {
						beanFactory.registerSingleton(name, (parent.getBean(name)));
					}
				}
			}
		}

	}

	private final class ResourceXmlApplicationContext extends GenericXmlApplicationContext {

		private final ApplicationContextHelper helper;

		/**
		 * @param parent
		 */
		public ResourceXmlApplicationContext(ConfigurableApplicationContext parent, Object... resources) {
            if (customClassLoader != null) {
                getBeanFactory().setBeanClassLoader(customClassLoader);
                getReader().setBeanClassLoader(customClassLoader);
                setClassLoader(customClassLoader);
            }

            helper = new ApplicationContextHelper(parent, this, resources) {
				@Override
				protected String generateId(Object... configs) {
					Resource[] resources = Arrays.copyOfRange(configs, 0, configs.length, Resource[].class);
  					try {
 						List<String> uris = new ArrayList<>();
 						for (Resource resource : resources) {
 							uris.add(resource.getURI().toString());
 						}
 						return StringUtils.collectionToCommaDelimitedString(uris);
  					}
  					catch (IOException e) {
 						return Arrays.toString(resources);
  					}
				}
				@Override
				protected void loadConfiguration(Object... configs) {
					Resource[] resources = Arrays.copyOfRange(configs, 0, configs.length, Resource[].class);
 					load(resources);
				}
			};
			refresh();
		}



		@Override
		protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
			super.prepareBeanFactory(beanFactory);
			helper.prepareBeanFactory(beanFactory);
		}

		@Override
		public String toString() {
			return "ResourceXmlApplicationContext:" + getId();
		}

	}

	private final class ResourceAnnotationApplicationContext extends AnnotationConfigApplicationContext {

		private final ApplicationContextHelper helper;

		public ResourceAnnotationApplicationContext(ConfigurableApplicationContext parent, Object... resources) {
            if (customClassLoader != null) {
                getBeanFactory().setBeanClassLoader(customClassLoader);
//                getReader().setBeanClassLoader(customClassLoader);
                setClassLoader(customClassLoader);
            }

			helper = new ApplicationContextHelper(parent, this, resources) {
				@Override
				protected String generateId(Object... configs) {
					if (allObjectsOfType(configs, Class.class)) {
						Class<?>[] types = Arrays.copyOfRange(configs, 0, configs.length, Class[].class);
						List<String> names = new ArrayList<String>();
						for (Class<?> type : types) {
							names.add(type.getName());
						}
						return StringUtils.collectionToCommaDelimitedString(names);
					}
					else {
						return Arrays.toString(configs);
					}
				}
				@Override
				protected void loadConfiguration(Object... configs) {
					if (allObjectsOfType(configs, Class.class)) {
						Class<?>[] types = Arrays.copyOfRange(configs, 0, configs.length, Class[].class);
						register(types);
					}
					else {
						String[] pkgs = Arrays.copyOfRange(configs, 0, configs.length, String[].class);
						scan(pkgs);
					}
				}
			};
			refresh();
		}

		@Override
		protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
			super.prepareBeanFactory(beanFactory);
			helper.prepareBeanFactory(beanFactory);
		}

		@Override
		public String toString() {
			return "ResourceAnnotationApplicationContext:" + getId();
		}

	}

}

